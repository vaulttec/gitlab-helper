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
package org.vaulttec.gitlab.helper;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum HelperPermission {
  GUEST("ROLE_GUEST"), USER("ROLE_USER"), ADMIN("ROLE_ADMIN");

  private String role;

  private HelperPermission(String role) {
    this.role = role;
  }

  private final static Map<String, HelperPermission> ENUM_NAME_MAP;
  static {
    ENUM_NAME_MAP = Arrays.stream(HelperPermission.values())
        .collect(Collectors.toMap(HelperPermission::getRole, Function.identity()));
  }

  public String getRole() {
    return role;
  }

  public static HelperPermission fromRole(String role) {
    return ENUM_NAME_MAP.get(role);
  }
}
