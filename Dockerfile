FROM gradle:9.3-jdk25-alpine AS builder
LABEL authors="ranfa"

WORKDIR /workspace

COPY settings.gradle.kts ./
COPY app/build.gradle.kts app/
COPY gradle/libs.versions.toml gradle/

RUN gradle :app:dependencies --no-daemon

COPY app/src app/src
RUN gradle :app:shadowJar --no-daemon -x test

FROM eclipse-temurin:25-jre-alpine

COPY --from=builder /workspace/app/build/libs/LunarMochiVox-bundle.jar /LunarMochiVox.jar

ENTRYPOINT ["java", "-jar", "/LunarMochiVox.jar"]
