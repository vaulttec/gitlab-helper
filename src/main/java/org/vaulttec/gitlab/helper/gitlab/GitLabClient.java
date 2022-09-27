/*
 * GitLab Helper
 * Copyright (c) 2022 Torsten Juergeleit
 * mailto:torsten AT vaulttec DOT org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaulttec.gitlab.helper.gitlab;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.vaulttec.gitlab.helper.gitlab.model.GLGroup;
import org.vaulttec.gitlab.helper.gitlab.model.GLMembership;
import org.vaulttec.gitlab.helper.gitlab.model.GLUser;
import org.vaulttec.gitlab.helper.gitlab.model.GLVariable;
import org.vaulttec.http.client.AbstractRestClient;
import org.vaulttec.http.client.LinkHeader;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GitLabClient extends AbstractRestClient {

  public static final String HEADER_NAME_AUTHENTICATION = "PRIVATE-TOKEN";
  protected static final ParameterizedTypeReference<GLUser> RESPONSE_TYPE_USER = new ParameterizedTypeReference<>() {
  };
  protected static final ParameterizedTypeReference<List<GLGroup>> RESPONSE_TYPE_GROUPS = new ParameterizedTypeReference<>() {
  };
  protected static final ParameterizedTypeReference<List<GLMembership>> RESPONSE_TYPE_MEMBERSHIPS = new ParameterizedTypeReference<>() {
  };
  protected static final ParameterizedTypeReference<List<GLVariable>> RESPONSE_TYPE_VARIABLES = new ParameterizedTypeReference<>() {
  };
  private static final Logger LOG = LoggerFactory.getLogger(GitLabClient.class);

  GitLabClient(GitLabClientConfig config, RestTemplateBuilder restTemplateBuilder) {
    super(config, restTemplateBuilder);
    prepareAuthenticationEntity(HEADER_NAME_AUTHENTICATION, config.getPersonalAccessToken());
  }

  public GLUser getUser(String personalAccessToken) {
    if (!StringUtils.hasText(personalAccessToken)) {
      throw new IllegalStateException("GitLab user's private access token required");
    }
    LOG.debug("Retrieving user with token {}", personalAccessToken);
    String apiCall = "/user";
    HttpEntity<String> authenticationEntity = createAuthenticationEntity(HEADER_NAME_AUTHENTICATION, personalAccessToken);
    Map<String, String> uriVariables = createVariablesMap();
    return makeReadApiCall(apiCall, HttpMethod.GET, authenticationEntity, RESPONSE_TYPE_USER, uriVariables);
  }

  public List<GLMembership> getGroupMemberships(long userId) {
    LOG.debug("Retrieving memberships for user {}", userId);
    String apiCall = "/users/{userId}/memberships?type=Namespace";
    Map<String, String> uriVariables = createVariablesMap("userId", Long.toString(userId));
    return makeReadListApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_MEMBERSHIPS, uriVariables);
  }

  public List<GLGroup> getGroups(String search, boolean withStatistics) {
    LOG.debug("Retrieving groups: search={}, withStatistics={}", search, withStatistics);
    String apiCall = "/groups?statistics={statistics}";
    Map<String, String> uriVariables = createVariablesMap("statistics", Boolean.toString(withStatistics));
    if (StringUtils.hasText(search)) {
      apiCall += "&search={search}";
      uriVariables.put("search", search);
    }
    return makeReadListApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_GROUPS, uriVariables);
  }

  public List<GLVariable> getGroupVariables(long groupId) {
    LOG.debug("Retrieving group variables: groupId={}", groupId);
    String apiCall = "/groups/{groupId}/variables";
    Map<String, String> uriVariables = createVariablesMap("groupId", Long.toString(groupId));
    return makeReadListApiCall(apiCall, HttpMethod.GET, RESPONSE_TYPE_VARIABLES, uriVariables);
  }

  public GLVariable createGroupVariable(long groupId, String key, String value, String... settings) {
    if (settings.length % 2 != 0) {
      throw new IllegalStateException("Key-value required - uneven number of settings");
    }
    LOG.debug("Creating group variable: groupId={}, key={}", groupId, key);
    String apiCall = "/groups/{groupId}/variables";
    Map<String, String> uriVariables = createVariablesMap("groupId", Long.toString(groupId));
    MultiValueMap<String, String> body = createSettingsMap(key, value, settings);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set(HEADER_NAME_AUTHENTICATION, authenticationEntity.getHeaders().getFirst(HEADER_NAME_AUTHENTICATION));
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(body, headers);
    return makeWriteApiCall(apiCall, HttpMethod.POST, entity, GLVariable.class, uriVariables);
  }

  public GLVariable updateGroupVariable(long groupId, String key, String value, String[] settings) {
    if (settings.length % 2 != 0) {
      throw new IllegalStateException("Key-value required - uneven number of settings");
    }
    LOG.debug("Updating group variable: groupId={}, key={}", groupId, key);
    String apiCall = "/groups/{groupId}/variables/{key}";
    Map<String, String> uriVariables = createVariablesMap("groupId", Long.toString(groupId), "key", key);
    MultiValueMap<String, String> body = createSettingsMap(null, value, settings);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.set(HEADER_NAME_AUTHENTICATION, authenticationEntity.getHeaders().getFirst(HEADER_NAME_AUTHENTICATION));
    HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<MultiValueMap<String, String>>(body, headers);
    return makeWriteApiCall(apiCall, HttpMethod.PUT, entity, GLVariable.class, uriVariables);
  }

  public boolean deleteGroupVariable(long groupId, String key) {
    LOG.debug("Deleting variable '{}' from group '{}'", key, groupId);
    String apiCall = "/groups/{groupId}/variables/{key}";
    Map<String, String> uriVariables = createVariablesMap("groupId", Long.toString(groupId), "key", key);
    return makeWriteApiCall(apiCall, HttpMethod.DELETE, uriVariables);
  }

  private static MultiValueMap<String, String> createSettingsMap(String key, String value, String[] settings) {
    MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
    if (key != null) {
      body.add("key", key);
    }
    body.add("value", value);
    for (int i = 0; i < settings.length; i += 2) {
      body.add(settings[i], settings[i + 1]);
    }
    return body;
  }

  protected <T> T makeReadApiCall(String apiCall, HttpMethod method, HttpEntity<String> authenticationEntity, ParameterizedTypeReference<T> typeReference, Map<String, String> uriVariables, HttpStatus... ignoreStatus) {
    String url = getApiUrl(apiCall);
    try {
      ResponseEntity<T> response = restTemplate.exchange(url, method, authenticationEntity, typeReference, uriVariables);
      return response.getBody();
    } catch (RestClientException e) {
      logException(method, uriVariables, url, e, ignoreStatus);
    }
    return null;
  }

  @Override
  protected <T> List<T> makeReadListApiCall(String apiCall, HttpMethod method, ParameterizedTypeReference<List<T>> typeReference, Map<String, String> uriVariables, HttpStatus... ignoreStatus) {
    String url = getApiUrl(apiCall + (apiCall.contains("?") ? "&" : "?") + "per_page={perPage}");
    uriVariables.put("perPage", perPageAsString());
    try {
      List<T> entities;
      ResponseEntity<List<T>> response = restTemplate.exchange(url, method, authenticationEntity, typeReference, uriVariables);
      LinkHeader linkHeader = LinkHeader.parse(response.getHeaders());
      if (linkHeader == null || !linkHeader.hasLink(LinkHeader.Rel.NEXT)) {
        entities = response.getBody();
      } else {
        entities = new ArrayList<>(response.getBody());
        do {
          URI nextResourceUri = linkHeader.getLink(LinkHeader.Rel.NEXT).getResourceUri();
          response = restTemplate.exchange(nextResourceUri, method, authenticationEntity, typeReference);
          entities.addAll(response.getBody());
          linkHeader = LinkHeader.parse(response.getHeaders());
        } while (linkHeader != null && linkHeader.hasLink(LinkHeader.Rel.NEXT));
      }
      return entities;
    } catch (RestClientException e) {
      logException(method, uriVariables, url, e, ignoreStatus);
    }
    return null;
  }
}
