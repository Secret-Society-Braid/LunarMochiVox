plugins {
  java
  application
  jacoco
  alias(libs.plugins.shadow)
}

group = "braid.society.secret"
version = "1.0-SNAPSHOT"

java {
  toolchain {
    languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get().toInt()))
  }
}

application {
  mainModule = "lunarmochivox.app"
  mainClass = "braid.society.secret.lunarmochivox.LunarMochiVoxApp"
}

dependencies {
  implementation(libs.jda)
  implementation(libs.logback.classic)
  implementation(libs.guava)

  testImplementation(platform(libs.junit.bom))
  testImplementation(libs.junit.jupiter)
  testRuntimeOnly(libs.junit.platform.launcher)
  testImplementation(libs.bundles.tests)
}

jacoco {
  toolVersion = libs.versions.jacoco.get()
}

tasks.test {
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required.set(true)
    html.required.set(true)
    csv.required.set(false)
  }
}

tasks.shadowJar {
  mergeServiceFiles()
  archiveFileName.set("LunarMochiVox-bundle.jar")
}
