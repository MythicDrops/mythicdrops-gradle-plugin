# mythicdrops-gradle-plugin

> Provides standard defaults for MythicDrops Gradle-based projects.

## Plugins

### Orchestration Project Plugin

The `dev.mythicdrops.gradle.project` plugin applies all the below plugins when applied to your root project.

#### Usage

Replace `x.y.z` in the snippet below with the version you want to use.

```kotlin
plugins {
    id("dev.mythicdrops.gradle.project") version "x.y.z"
}
```

### Root Project Plugin

The `dev.mythicdrops.gradle.project.root` plugin will configure the following when applied to your root project:

- Applies the [`org.shipkit.shipkit-auto-version`](https://github.com/shipkit/shipkit-auto-version) Gradle plugin

#### Usage

Replace `x.y.z` in the snippet below with the version you want to use.

```kotlin
plugins {
    id("dev.mythicdrops.gradle.project.root") version "x.y.z"
}
```

### Base Project Plugin

The `dev.mythicdrops.gradle.project.base` plugin will configure the following when applied to any project:

- Applies the [`nebula.project`](https://github.com/nebula-plugins/nebula-project-plugin) Gradle plugin
- Applies the [`com.adarshr.test-logger`](https://github.com/radarsh/gradle-test-logger-plugin) Gradle plugin
  - Configures the `com.adarshr.test-logger` plugin to use
      the [Mocha theme](https://github.com/radarsh/gradle-test-logger-plugin#mocha-theme)
- Configures all test tasks to use JUnit Jupiter

#### Usage

Replace `x.y.z` in the snippet below with the version you want to use.

```kotlin
plugins {
    id("dev.mythicdrops.gradle.project.base") version "x.y.z"
}
```

### GitHub Release Plugin

The `dev.mythicdrops.gradle.release.github` plugin will configure the following when applied to your root project:

- Applies the [`org.shipkit.shipkit-changelog`](https://github.com/shipkit/shipkit-changelog) Gradle plugin
- Applies the [`org.shipkit.shipkit-github-release`](https://github.com/shipkit/shipkit-changelog) Gradle plugin

#### Usage

Replace `x.y.z` in the snippet below with the version you want to use.

```kotlin
plugins {
    id("dev.mythicdrops.gradle.release.github") version "x.y.z"
}
```

Configure the repository you're running the project in by adding this snippet to your root `build.gradle.kts`:

```kotlin
mythicDropsRelease {
    repository = "GitHubOrganization/GitHubRepository"
}
```

### Java Plugin

The `dev.mythicdrops.gradle.convention.java` plugin will configure the following when applied to any project that also
has the `java` plugin applied:

- Configures the project to compile targeting JDK 17
- Configures the project to pass the `-parameters` javac flag when compiling
- Applies the [`jacoco`](https://docs.gradle.org/current/userguide/jacoco_plugin.html) Gradle plugin
  - Configures the `jacoco` plugin to use JaCoCo 0.8.7
- Configures test tasks to be finalized by running a JaCoCo code coverage report

#### Usage

Replace `x.y.z` in the snippet below with the version you want to use.

```kotlin
plugins {
    id("dev.mythicdrops.gradle.convention.java") version "x.y.z"
}
```

You can override the JDK version the same way you'd override it in a normal Java project:
```kotlin
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
```

### Kotlin JVM Plugin

The `dev.mythicdrops.gradle.conventions.kotlin.jvm` plugin will configure the following when applied to any project that
also has the `org.jetbrains.kotlin.jvm` plugin applied:

- Configures the project to compile targeting JDK 16
- Configures the project to pass the `-parameters` javac flag when compiling
- Applies the [`detekt`](https://detekt.github.io/detekt/gradle.html) Gradle plugin
- Applies the [`ktlint`](https://github.com/JLLeitschuh/ktlint-gradle) Gradle plugin
  - Configures the `ktlint` plugin to use KTLint 0.43.0
- Applies the [`dokka`](https://github.com/Kotlin/dokka) Gradle plugin
- Configures Javadoc JARs to include dokka output

#### Usage

Replace `x.y.z` in the snippet below with the version you want to use.

```kotlin
plugins {
    id("dev.mythicdrops.gradle.convention.kotlin.jvm") version "x.y.z"
}
```

### Maven Publish Plugin

The `dev.mythicdrops.gradle.conventions.maven-publish` plugin will configure the following when applied to any project
that also has the `maven-publih` plugin applied:

- Configures published Maven POMs to include `compileOnly` dependencies as `provided`
- Configures published Maven POMs to have an MIT License
- Configures the project to publish to Sonatype OSSRH if `SONATYPE_USER` and `SONATYPE_PWD` environment variables are
  available
- Configures the project to sign published artifacts with GPG if `PGP_KEY` and `PGP_PWD` environment variables are
  available

#### Usage

Replace `x.y.z` in the snippet below with the version you want to use.

```kotlin
plugins {
    id("dev.mythicdrops.gradle.convention.kotlin.jvm") version "x.y.z"
}
```
