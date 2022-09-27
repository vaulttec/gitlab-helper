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
package org.vaulttec.gitlab.helper.api.model;

import org.vaulttec.gitlab.helper.gitlab.model.GLPermission;

import java.util.Objects;

public class Group {
  private Long id;
  private String name;
  private GLPermission permission;

  public Group(Long id, String name, GLPermission permission) {
    this.id = id;
    this.name = name;
    this.permission = permission;
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public GLPermission getPermission() {
    return permission;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Group group = (Group) o;

    return Objects.equals(id, group.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Group{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", permission=" + permission +
        '}';
  }
}
