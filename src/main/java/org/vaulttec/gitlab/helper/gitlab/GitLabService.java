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

import org.springframework.stereotype.Service;
import org.vaulttec.gitlab.helper.gitlab.model.GLMembership;
import org.vaulttec.gitlab.helper.gitlab.model.GLUser;
import org.vaulttec.gitlab.helper.gitlab.model.GLVariable;

import java.util.ArrayList;
import java.util.List;

@Service
public class GitLabService {

  private final GitLabClient client;

  public GitLabService(GitLabClient client) {
    this.client = client;
  }

  public GLUser getUser(String personalAccessToken) {
    return client.getUser(personalAccessToken);
  }

  public List<GLMembership> getGroupMemberships(long userId) {
    return client.getGroupMemberships(userId);
  }

  public List<GLVariable> getGroupVariables(long groupId) {
    return client.getGroupVariables(groupId);
  }

  public GLVariable createGroupVariable(long groupId, String key, String value, String variableType, Boolean isProtected, Boolean isMasked, String environmentScope) {
    List<String> settings = createVariableSettings(variableType, isProtected, isMasked, environmentScope);
    return client.createGroupVariable(groupId, key, value, settings.toArray(new String[0]));
  }

  public GLVariable updateGroupVariable(long groupId, String key, String value, String variableType, Boolean isProtected, Boolean isMasked, String environmentScope) {
    List<String> settings = createVariableSettings(variableType, isProtected, isMasked, environmentScope);
    return client.updateGroupVariable(groupId, key, value, settings.toArray(new String[0]));
  }

  public boolean deleteGroupVariable(long groupId, String key) {
    return client.deleteGroupVariable(groupId, key);
  }

  private static List<String> createVariableSettings(String variableType, Boolean isProtected, Boolean isMasked, String environmentScope) {
    List<String> settings = new ArrayList<>();
    if (variableType != null) {
      settings.add("variable_type");
      settings.add(variableType);
    }
    if (isProtected != null) {
      settings.add("protected");
      settings.add(isProtected.toString());
    }
    if (isMasked != null) {
      settings.add("masked");
      settings.add(isMasked.toString());
    }
    if (environmentScope != null) {
      settings.add("environment_scope");
      settings.add(environmentScope);
    }
    return settings;
  }
}
