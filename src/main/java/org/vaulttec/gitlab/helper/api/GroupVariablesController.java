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
package org.vaulttec.gitlab.helper.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.vaulttec.gitlab.helper.api.model.Group;
import org.vaulttec.gitlab.helper.api.model.Variable;
import org.vaulttec.gitlab.helper.gitlab.GitLabService;
import org.vaulttec.gitlab.helper.gitlab.model.GLMembership;
import org.vaulttec.gitlab.helper.gitlab.model.GLPermission;
import org.vaulttec.gitlab.helper.gitlab.model.GLUser;
import org.vaulttec.gitlab.helper.gitlab.model.GLVariable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class GroupVariablesController {
  private static final Logger LOG = LoggerFactory.getLogger(GroupVariablesController.class);

  private final GitLabService service;

  public GroupVariablesController(GitLabService service) {
    this.service = service;
  }

  @GetMapping("/groups")
  public List<Group> getGroups(@RequestHeader("PRIVATE-TOKEN") String personalAccessToken) {
    GLUser user = getUser(personalAccessToken);
    LOG.info("Retrieving groups of user {}", user.getUsername());
    List<GLMembership> groupMemberships = getGroupMemberships(user);
    return groupMemberships.stream().map(membership -> new Group(membership.getSourceId(), membership.getSourceName(), membership.getPermission())).collect(Collectors.toList());
  }

  @GetMapping("/groups/{groupId}/variables")
  public List<Variable> getGroupVariables(@RequestHeader("PRIVATE-TOKEN") String personalAccessToken, @PathVariable long groupId) {
    GLUser user = getUser(personalAccessToken);
    LOG.info("Retrieving group variables of group {} for user {}", groupId, user.getUsername());
    GLMembership groupMembership = getGroupMembership(user, groupId);
    List<GLVariable> variables = getGroupVariables(groupMembership);
    return variables.stream().map(variable -> new Variable(variable.getKey(), variable.getValue(), variable.getVariableType(), variable.getIsProtected(), variable.getIsMasked(), variable.getEnvironmentScope())).collect(Collectors.toList());
  }

  @PostMapping(path = "/groups/{groupId}/variables", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public Variable createGroupVariable(@RequestHeader("PRIVATE-TOKEN") String personalAccessToken, @PathVariable long groupId, Variable variable) {
    GLUser user = getUser(personalAccessToken);
    LOG.info("Creating variable '{}' in group {} for user {}", variable, groupId, user.getUsername());
    checkVariable(variable);
    GLMembership groupMembership = getGroupMembership(user, groupId);
    List<GLVariable> variables = getGroupVariables(groupMembership);
    if (variables.stream().anyMatch(v -> v.getKey().equals(variable.getKey()))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group variable already exists");
    }
    GLVariable newVariable = service.createGroupVariable(groupId, variable.getKey(), variable.getValue(), variable.getVariableType(), variable.getIsProtected(), variable.getIsMasked(), variable.getEnvironmentScope());
    if (newVariable == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Creating group variable failed");
    }
    return new Variable(newVariable.getKey(), newVariable.getValue(), newVariable.getVariableType(), newVariable.getIsProtected(), newVariable.getIsMasked(), newVariable.getEnvironmentScope());
  }

  @PutMapping(path = "/groups/{groupId}/variables", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public Variable updateGroupVariable(@RequestHeader("PRIVATE-TOKEN") String personalAccessToken, @PathVariable long groupId, Variable variable) {
    GLUser user = getUser(personalAccessToken);
    LOG.info("Updating variable '{}' in group {} for user {}", variable, groupId, user.getUsername());
    checkVariable(variable);
    GLMembership groupMembership = getGroupMembership(user, groupId);
    List<GLVariable> variables = getGroupVariables(groupMembership);
    if (!variables.stream().anyMatch(v -> v.getKey().equals(variable.getKey()))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group variable does not exist");
    }
    GLVariable updatedVariable = service.updateGroupVariable(groupId, variable.getKey(), variable.getValue(), variable.getVariableType(), variable.getIsProtected(), variable.getIsMasked(), variable.getEnvironmentScope());
    if (updatedVariable == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Updating group variable failed");
    }
    return new Variable(updatedVariable.getKey(), updatedVariable.getValue(), updatedVariable.getVariableType(), updatedVariable.getIsProtected(), updatedVariable.getIsMasked(), updatedVariable.getEnvironmentScope());
  }

  @DeleteMapping("/groups/{groupId}/variables/{key}")
  public void deleteGroupVariable(@RequestHeader("PRIVATE-TOKEN") String personalAccessToken, @PathVariable long groupId, @PathVariable String key) {
    GLUser user = getUser(personalAccessToken);
    LOG.info("Deleting variable '{}' in group {} for user {}", key, groupId, user.getUsername());
    if (!StringUtils.hasText(key)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required param 'key'");
    }
    GLMembership groupMembership = getGroupMembership(user, groupId);
    List<GLVariable> variables = getGroupVariables(groupMembership);
    if (!variables.stream().anyMatch(v -> v.getKey().equals(key))) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Group variable does not exist");
    }
    if (!service.deleteGroupVariable(groupId, key)) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Deleting group variable failed");
    }
  }

  private GLUser getUser(String personalAccessToken) {
    GLUser user = service.getUser(personalAccessToken);
    if (user == null) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid GitLab user's personal access token");
    }
    return user;
  }

  private GLMembership getGroupMembership(GLUser user, long groupId) {
    List<GLMembership> groupMemberships = getGroupMemberships(user);
    Optional<GLMembership> groupMembership = groupMemberships.stream().filter(membership -> membership.getSourceId().equals(groupId)).findFirst();
    if (groupMembership.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "GitLab user is not member of group");
    }
    if (groupMembership.get().getPermission().compareAccessLevel(GLPermission.MAINTAINER) < 0) {
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "GitLab user has insufficient access permissions in group - at least MAINTAINER permission is required");
    }
    return groupMembership.get();
  }

  private List<GLMembership> getGroupMemberships(GLUser user) {
    List<GLMembership> groupMemberships = service.getGroupMemberships(user.getId());
    if (groupMemberships == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Retrieving group membership failed");
    }
    return groupMemberships;
  }

  private List<GLVariable> getGroupVariables(GLMembership groupMembership) {
    List<GLVariable> variables = service.getGroupVariables(groupMembership.getSourceId());
    if (variables == null) {
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Retrieving group variables failed");
    }
    return variables;
  }

  private static void checkVariable(Variable variable) {
    if (!StringUtils.hasText(variable.getKey())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required param 'key'");
    }
    if (!StringUtils.hasText(variable.getValue())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing required param 'value'");
    } else if (variable.getIsMasked() != null && variable.getIsMasked().booleanValue() && !Variable.isValueMaskable(variable.getValue())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Param 'value' is not maskable - must fulfill regex '" + Variable.MASKABLE_VARIABLE + "'");
    }
  }
}
