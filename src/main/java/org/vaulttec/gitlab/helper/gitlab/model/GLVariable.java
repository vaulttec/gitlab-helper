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
public class GLVariable {
  private String key;
  private String value;
  @JsonAlias("variable_type")
  private String variableType;
  @JsonAlias("protected")
  private boolean isProtected;
  @JsonAlias("masked")
  private boolean isMasked;
  @JsonAlias("environment_scope")
  private String environmentScope;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getVariableType() {
    return variableType;
  }

  public void setVariableType(String variableType) {
    this.variableType = variableType;
  }

  public boolean getIsProtected() {
    return isProtected;
  }

  public void setIsProtected(boolean isProtected) {
    this.isProtected = isProtected;
  }

  public boolean getIsMasked() {
    return isMasked;
  }

  public void setIsMasked(boolean isMasked) {
    this.isMasked = isMasked;
  }

  public String getEnvironmentScope() {
    return environmentScope;
  }

  public void setEnvironmentScope(String environmentScope) {
    this.environmentScope = environmentScope;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GLVariable that = (GLVariable) o;

    return Objects.equals(key, that.key);
  }

  @Override
  public int hashCode() {
    return key != null ? key.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "GLVariable{" +
        "key='" + key + '\'' +
        ", value='" + value + '\'' +
        ", variableType='" + variableType + '\'' +
        ", isProtected=" + isProtected +
        ", isMasked=" + isMasked +
        ", environmentScope='" + environmentScope + '\'' +
        '}';
  }
}
