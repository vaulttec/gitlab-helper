GitLab Helper  [![Build Status](https://github.com/vaulttec/gitlab-helper/actions/workflows/build.yml/badge.svg)](https://github.com/vaulttec/gitlab-helper/actions/workflows/build.yml) [![Docker Image](https://img.shields.io/docker/pulls/tjuerge/gitlab-helper.svg)](https://hub.docker.com/r/tjuerge/gitlab-helper)
===============

Spring Boot application with Web API for GitLab operations which require owner or system administration permission


## Run the project with

```
./mvnw clean spring-boot:run -Dspring-boot.run.profiles=test
```

Open browser to http://localhost:8080/


## To package the project run

```
./mvnw clean package
```
