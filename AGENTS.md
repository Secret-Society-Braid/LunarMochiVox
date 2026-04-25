# AGENTS Guide for LunarMochiVox

## Scope

- This repository is a single-module Gradle Java app: `settings.gradle.kts` includes only `app`.
- Runtime entrypoint is configured in `app/build.gradle.kts` via
  `application { mainModule/mainClass }`.

## Big Picture Architecture

- Core code lives in `app/src/main/java/braid/society/secret/lunarmochivox/`.
- JPMS is enabled (`app/src/main/java/module-info.java`), so API/module boundaries are explicit.
- Current startup flow is minimal: `LunarMochiVoxApp.main(...)` logs startup arguments using SLF4J.
- Logging is routed through Logback config at `app/src/main/resources/logback.xml` (console +
  rolling file under `logs/`).
- `JDA` is declared as a dependency in `app/build.gradle.kts`; Discord-facing logic should be added
  behind the app entrypoint and reflected in `module-info.java` when used.

## Developer Workflows

- Windows local build/test: `gradlew.bat test jacocoTestReport`.
- Linux/macOS local build/test: `./gradlew test jacocoTestReport`.
- Build runnable fat jar: `gradlew.bat shadowJar` (artifact:
  `app/build/libs/LunarMochiVox-bundle.jar`).
- CI (`.github/workflows/gradle-ci.yml`) runs `./gradlew --info test jacocoTestReport` on JDK 25.

## Command Execution Policy

- Do not auto-run build/test/shell commands by default.
- Provide suggested commands only; the user executes and verifies them via IDE features.
- When reporting status, clearly separate "edited" from "user-run verification pending".

## Project Conventions (Observed)

- Keep package namespace under `braid.society.secret.lunarmochivox`.
- Centralize versions in `gradle/libs.versions.toml`; prefer version-catalog aliases over inline
  versions.
- Tests use JUnit Jupiter + Truth (`EnvironmentTest.java` shows both assertion styles).
- JaCoCo reports are always generated after tests (
  `tasks.test.finalizedBy(tasks.jacocoTestReport)`).
- `shadowJar` merges service files; keep this when adding libraries using `META-INF/services`.

## Integration and Dependency Notes

- Java toolchain is pinned to `25` (`app/build.gradle.kts`, `libs.versions.toml`), matching CI.
- `dependencyResolutionManagement` is set to `FAIL_ON_PROJECT_REPOS`; add repos centrally in
  `settings.gradle.kts` only.
- Dependabot updates Gradle and GitHub Actions weekly (`.github/dependabot.yml`).

## Safe Change Checklist for Agents

- If adding imports from new libraries, update `module-info.java` `requires` clauses as needed.
- If moving/renaming entrypoint, update `application.mainClass` and suggest verifying launch.
- If changing logging behavior, keep both console and rolling-file appenders unless intentionally
  removing one.
- Suggest running at least `test` and `jacocoTestReport` before finishing; do not run them
  automatically. If commands were not run by the user, clearly report verification as pending.
