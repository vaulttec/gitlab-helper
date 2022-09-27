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
package org.vaulttec.gitlab.helper.gitlab.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GLMembership {
  @JsonAlias("source_id")
  private Long sourceId;
  @JsonAlias("source_name")
  private String sourceName;
  @JsonAlias("source_type")
  private String sourceType;
  @JsonAlias("access_level")
  private GLPermission permission;

  public Long getSourceId() {
    return sourceId;
  }

  public void setSourceId(Long sourceId) {
    this.sourceId = sourceId;
  }

  public String getSourceName() {
    return sourceName;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public GLPermission getPermission() {
    return permission;
  }

  public void setPermission(GLPermission permission) {
    this.permission = permission;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GLMembership that = (GLMembership) o;

    return Objects.equals(sourceId, that.sourceId);
  }

  @Override
  public int hashCode() {
    return sourceId != null ? sourceId.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "GLMembership{" +
        "sourceId='" + sourceId + '\'' +
        ", sourceName='" + sourceName + '\'' +
        ", sourceType='" + sourceType + '\'' +
        ", permission=" + permission +
        '}';
  }
}
