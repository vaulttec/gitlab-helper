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

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GLUser {
  public static final String CUSTOM_ATTRIBUTE_JOINED = "community_joined";
  public static final DateTimeFormatter JOINED_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  private Long id;
  private String username;
  private String name;
  private String email;
  @JsonAlias("avatar_url")
  private URL avatar;
  @JsonAlias("web_url")
  private URL profile;
  private String bio;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public URL getAvatar() {
    return avatar;
  }

  public void setAvatar(URL avatar) {
    this.avatar = avatar;
  }

  public URL getProfile() {
    return profile;
  }

  public void setProfile(URL profile) {
    this.profile = profile;
  }

  public String getBio() {
    return bio;
  }

  public void setBio(String bio) {
    this.bio = bio;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GLUser glUser = (GLUser) o;

    return Objects.equals(id, glUser.id);
  }

  @Override
  public int hashCode() {
    return id != null ? id.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "GLUser{" +
        "id=" + id +
        ", username='" + username + '\'' +
        ", name='" + name + '\'' +
        ", email='" + email + '\'' +
        ", avatar=" + avatar +
        ", profile=" + profile +
        ", bio='" + bio + '\'' +
        '}';
  }
}
