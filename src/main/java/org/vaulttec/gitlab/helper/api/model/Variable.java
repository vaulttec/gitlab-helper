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

import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;

public class Variable {
  // RegEx for GitLab maskable CI variable https://docs.gitlab.com/ee/ci/variables/index.html#mask-a-cicd-variable
  public static final Pattern MASKABLE_VARIABLE = Pattern.compile("^[A-Za-z0-9@:.~]{8,}$");

  private String key;
  private String value;
  private String variableType;
  private Boolean isProtected;
  private Boolean isMasked;
  private String environmentScope;

  public Variable(String key, String value, String variableType, Boolean isProtected, Boolean isMasked, String environmentScope) {
    this.key = key;
    this.value = value;
    this.variableType = variableType;
    this.isProtected = isProtected;
    this.isMasked = isMasked;
    this.environmentScope = environmentScope;
  }

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

  public static final boolean isValueMaskable(String value) {
    return StringUtils.hasText(value) && MASKABLE_VARIABLE.matcher(value).matches();
  }

  public String getVariableType() {
    return variableType;
  }

  public void setVariableType(String variableType) {
    this.variableType = variableType;
  }

  public Boolean getIsProtected() {
    return isProtected;
  }

  public void setIsProtected(Boolean isProtected) {
    this.isProtected = isProtected;
  }

  public Boolean getIsMasked() {
    return isMasked;
  }

  public void setIsMasked(Boolean isMasked) {
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

    Variable variable = (Variable) o;

    return Objects.equals(key, variable.key);
  }

  @Override
  public int hashCode() {
    return key != null ? key.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Variable{" +
        "key='" + key + '\'' +
        ", value='" + value + '\'' +
        ", variableType='" + variableType + '\'' +
        ", isProtected=" + isProtected +
        ", isMasked=" + isMasked +
        ", environmentScope='" + environmentScope + '\'' +
        '}';
  }
}
