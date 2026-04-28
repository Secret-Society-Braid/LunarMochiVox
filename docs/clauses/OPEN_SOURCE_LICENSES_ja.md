# オープンソースライセンス

このドキュメントでは、このプロジェクトで使われているOSSソフトウェアやライブラリ、サービスについて記述しています。

## 目次

- [1. Runtime dependencies](#1-ランタイム依存関係)
- [2. Test dependencies](#2-テスト時依存関係)
- [3. Third-party applications/services](#3-サードパーティアプリケーションサービス)

## 1. ランタイム依存関係

| Component            | Module / Coordinate                              | Version Source                               | License (SPDX)             | Upstream License                                                  |
|----------------------|--------------------------------------------------|----------------------------------------------|----------------------------|-------------------------------------------------------------------|
| JDA                  | `net.dv8tion:JDA`                                | `libs.versions.toml` (`jda`)                 | `Apache-2.0`               | https://github.com/discord-jda/JDA/blob/master/LICENSE            |
| logback-classic (*1) | `ch.qos.logback:logback-classic`                 | `libs.versions.toml` (`logback`)             | `EPL-1.0 OR LGPL-2.1-only` | https://logback.qos.ch/license.html                               |
| SLF4J (推移的) (*2)     | `org.slf4j:*`                                    | transitive                                   | `MIT`                      | https://www.slf4j.org/license.html                                |
| Guava                | `com.google.guava:guava`                         | `libs.versions.toml` (`guava`)               | `Apache-2.0`               | https://github.com/google/guava/blob/master/LICENSE               |
| jackson-core         | `com.fasterxml.jackson.core:jackson-core`        | `libs.versions.toml` (`jackson`)             | `Apache-2.0`               | https://github.com/FasterXML/jackson-core/blob/2.x/LICENSE        |
| jackson-databind     | `com.fasterxml.jackson.core:jackson-databind`    | `libs.versions.toml` (`jackson`)             | `Apache-2.0`               | https://github.com/FasterXML/jackson-databind/blob/2.x/LICENSE    |
| jackson-annotations  | `com.fasterxml.jackson.core:jackson-annotations` | `libs.versions.toml` (`jackson-annotations`) | `Apache-2.0`               | https://github.com/FasterXML/jackson-annotations/blob/2.x/LICENSE |

## 2. テスト時依存関係

> テスト時依存関係は通常、ビルド後のアプリケーションやイメージには含まれません。
> しかしながら、これらのOSSライブラリやフレームワークは、プロジェクトを健全に保つうえで
> 非常に重要な役割を果たしているため、ここに再褐しています。

| Component                | Module / Coordinate                           | Version Source                   | License (SPDX) | Upstream License                                                   |
|--------------------------|-----------------------------------------------|----------------------------------|----------------|--------------------------------------------------------------------|
| JUnit Jupiter / Platform | `org.junit.jupiter:*`, `org.junit.platform:*` | `libs.versions.toml` (`junit`)   | `EPL-2.0`      | https://github.com/junit-team/junit-framework/blob/main/LICENSE.md |
| Truth                    | `com.google.truth:truth`                      | `libs.versions.toml` (`truth`)   | `Apache-2.0`   | https://github.com/google/truth/blob/master/LICENSE                |
| Mockito                  | `org.mockito:mockito-core`                    | `libs.versions.toml` (`mockito`) | `MIT`          | https://github.com/mockito/mockito/blob/main/LICENSE               |

## 3. サードパーティアプリケーション/サービス

| Name          | Usage Model                       | License                                                           | Reference                                                       |
|---------------|-----------------------------------|-------------------------------------------------------------------|-----------------------------------------------------------------|
| VOICEVOX (*3) | Usage for VOICEVOX ENGINE web API | `LGPL-3.0-or-later` or Licenses not requiring source code release | https://github.com/VOICEVOX/voicevox_engine/blob/master/LICENSE |

(*1) このプロジェクトでは、logback-classicをLGPL v2.1+ でライセンスされているライブラリとして扱います。

(*2) SLF4Jはlogback-classicからの推移的依存関係です。
いかなる直接的な依存関係の宣言もプロジェクト内には含まれていません。

(*3) このプロジェクトでは、VOICEVOX ENGINEをLGPL v3.0+ でライセンスされているライブラリとして扱います。
VOICEVOX ENGINE内に含まれる個々のボイスバンクの使用宣言はこのリストには含まれていません。
それらの情報については、別途[VOICEVOXボイスバンク使用宣言（英語のみ）](./VOICEVOX_VOICEBANKS.md)
をご参照ください。
