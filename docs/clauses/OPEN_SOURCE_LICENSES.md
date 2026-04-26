# Open Source Licenses

This document describes the open-source software components which power this project.

## Table of Contents

- [1. Runtime dependencies](#1-runtime-dependencies)
- [2. Test dependencies](#2-test-dependencies)
- [3. Third-party applications/services](#3-third-party-applicationsservices)

## 1. Runtime dependencies

| Component               | Module / Coordinate                              | Version Source                               | License (SPDX)             | Upstream License                                                  |
|-------------------------|--------------------------------------------------|----------------------------------------------|----------------------------|-------------------------------------------------------------------|
| JDA                     | `net.dv8tion:JDA`                                | `libs.versions.toml` (`jda`)                 | `Apache-2.0`               | https://github.com/discord-jda/JDA/blob/master/LICENSE            |
| logback-classic (*1)    | `ch.qos.logback:logback-classic`                 | `libs.versions.toml` (`logback`)             | `EPL-1.0 OR LGPL-2.1-only` | https://logback.qos.ch/license.html                               |
| SLF4J (transitive) (*2) | `org.slf4j:*`                                    | transitive                                   | `MIT`                      | https://www.slf4j.org/license.html                                |
| Guava                   | `com.google.guava:guava`                         | `libs.versions.toml` (`guava`)               | `Apache-2.0`               | https://github.com/google/guava/blob/master/LICENSE               |
| jackson-core            | `com.fasterxml.jackson.core:jackson-core`        | `libs.versions.toml` (`jackson`)             | `Apache-2.0`               | https://github.com/FasterXML/jackson-core/blob/2.x/LICENSE        |
| jackson-databind        | `com.fasterxml.jackson.core:jackson-databind`    | `libs.versions.toml` (`jackson`)             | `Apache-2.0`               | https://github.com/FasterXML/jackson-databind/blob/2.x/LICENSE    |
| jackson-annotations     | `com.fasterxml.jackson.core:jackson-annotations` | `libs.versions.toml` (`jackson-annotations`) | `Apache-2.0`               | https://github.com/FasterXML/jackson-annotations/blob/2.x/LICENSE |

## 2. Test dependencies

> Test dependencies are not included in the app or docker build,
> which means there is no need to show licenses for them here,
> but these are a huge help for this project.

| Component                | Module / Coordinate                           | Version Source                   | License (SPDX) | Upstream License                                                   |
|--------------------------|-----------------------------------------------|----------------------------------|----------------|--------------------------------------------------------------------|
| JUnit Jupiter / Platform | `org.junit.jupiter:*`, `org.junit.platform:*` | `libs.versions.toml` (`junit`)   | `EPL-2.0`      | https://github.com/junit-team/junit-framework/blob/main/LICENSE.md |
| Truth                    | `com.google.truth:truth`                      | `libs.versions.toml` (`truth`)   | `Apache-2.0`   | https://github.com/google/truth/blob/master/LICENSE                |
| Mockito                  | `org.mockito:mockito-core`                    | `libs.versions.toml` (`mockito`) | `MIT`          | https://github.com/mockito/mockito/blob/main/LICENSE               |

## 3. Third-party applications/services

| Name          | Usage Model                       | License                                                           | Reference                                                       |
|---------------|-----------------------------------|-------------------------------------------------------------------|-----------------------------------------------------------------|
| VOICEVOX (*3) | Usage for VOICEVOX ENGINE web API | `LGPL-3.0-or-later` or Licenses not requiring source code release | https://github.com/VOICEVOX/voicevox_engine/blob/master/LICENSE |

(*1) In this project, we treat logback-classic as an LGPL v2.1+ library.

(*2) SLF4J is the transitive dependency of logback-classic. Any direct declaration of SLF4J is not
included in this list.

(*3) We treat VOICEVOX ENGINE as an LGPL v3.0+ library.
Individual voicebank usage declarations are not included in this list. Please refer to
the [VOICEVOX voicebank usage declaration](./VOICEVOX_VOICEBANKS.md) for such information.
