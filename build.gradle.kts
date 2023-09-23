plugins {
    `kotlin-dsl`
    `maven-publish`
    id("org.jlleitschuh.gradle.ktlint")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.dokka")
    id("com.gradle.plugin-publish")
    id("org.shipkit.shipkit-auto-version")
    id("org.shipkit.shipkit-changelog")
    id("org.shipkit.shipkit-github-release")
    id("com.adarshr.test-logger")
}

group = "dev.mythicdrops"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withJavadocJar()
    withSourcesJar()
}

@Suppress("UnstableApiUsage")
gradlePlugin {
    website.set("https://github.com/MythicDrops/mythicdrops-gradle-plugin")
    vcsUrl.set("https://github.com/MythicDrops/mythicdrops-gradle-plugin")
    plugins {
        create("mythicDropsProject") {
            id = "dev.mythicdrops.gradle.project"
            displayName = "mythicDropsGradleProject"
            description = "Orchestration plugin for all MythicDrops Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.MythicDropsPlugin"
            tags.set(listOf("kotlin", "pixeloutlaw", "convention"))
        }
        create("mythicDropsProjectRoot") {
            id = "dev.mythicdrops.gradle.project.root"
            displayName = "mythicDropsGradleProjectRoot"
            description = "Common conventions for MythicDrops root Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.projects.MythicDropsRootPlugin"
            tags.set(listOf("kotlin", "pixeloutlaw", "convention"))
        }
        create("mythicDropsProjectBase") {
            id = "dev.mythicdrops.gradle.project.base"
            displayName = "mythicDropsGradleProjectBase"
            description = "Common conventions for all MythicDrops Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.projects.MythicDropsBasePlugin"
            tags.set(listOf("kotlin", "pixeloutlaw", "convention"))
        }
        create("mythicDropsReleaseGitHub") {
            id = "dev.mythicdrops.gradle.release.github"
            displayName = "mythicDropsGradleReleaseGitHub"
            description = "Create a GitHub Release via GitHub Actions for all MythicDrops Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.release.MythicDropsReleaseGitHubPlugin"
            tags.set(listOf("kotlin", "pixeloutlaw", "convention"))
        }
        create("mythicDropsConventionJava") {
            id = "dev.mythicdrops.gradle.convention.java"
            displayName = "mythicDropsGradleConventionJava"
            description = "Common conventions for all MythicDrops Java Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.conventions.MythicDropsJavaPlugin"
            tags.set(listOf("kotlin", "pixeloutlaw", "convention"))
        }
        create("mythicDropsConventionJavaPlatform") {
            id = "dev.mythicdrops.gradle.convention.java-platform"
            displayName = "mythicDropsGradleConventionJavaPlatform"
            description = "Common conventions for all MythicDrops Java Platform Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.conventions.MythicDropsJavaPlatformPlugin"
            tags.set(listOf("kotlin", "pixeloutlaw", "convention"))
        }
        create("mythicDropsConventionJvmTestSuite") {
            id = "dev.mythicdrops.gradle.convention.jvm-test-suite"
            displayName = "mythicDropsGradleConventionJvmSuite"
            description = "Common conventions for all MythicDrops JVM Test Suite Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.conventions.MythicDropsJvmTestSuitePlugin"
            tags.set(listOf("kotlin", "pixeloutlaw", "convention"))
        }
        create("mythicDropsConventionKotlinJvm") {
            id = "dev.mythicdrops.gradle.convention.kotlin.jvm"
            displayName = "mythicDropsGradleConventionKotlinJvm"
            description = "Common conventions for all MythicDrops Kotlin JVM Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.conventions.MythicDropsKotlinJvmPlugin"
            tags.set(listOf("kotlin", "pixeloutlaw", "convention"))
        }
        create("mythicDropsConventionsMavenPublish") {
            id = "dev.mythicdrops.gradle.convention.maven-publish"
            displayName = "mythicDropsGradle"
            description = "Common conventions for all MythicDrops Maven Publishing Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.conventions.MythicDropsMavenPublishPlugin"
            tags.set(listOf("kotlin", "pixeloutlaw", "convention"))
        }
    }
}

ktlint {
    version.set("1.0.0")
}

tasks {
    // get dokkaJavadoc task and make javadocJar depend on it
    val dokkaJavadoc by this
    getByName<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        from(dokkaJavadoc)
    }

    // use JUnit Jupiter
    withType<Test> {
        useJUnitPlatform()
    }

    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        }
    }

    getByName("generateChangelog") {
        dependsOn(
            "compileJava",
            "compileKotlin",
            "javadoc",
            "dokkaJavadoc",
            "javadocJar",
            "sourcesJar",
            "generateMetadataFileForPluginMavenPublication",
            "generatePomFileForPluginMavenPublication",
        )
    }

    // Make GitHub release depend on generating a changelog
    val generateChangelog =
        getByName<org.shipkit.changelog.GenerateChangelogTask>("generateChangelog") {
            previousRevision = project.ext.get("shipkit-auto-version.previous-tag")?.toString()
            githubToken = System.getenv("GITHUB_TOKEN")
            repository = "MythicDrops/mythicdrops-gradle-plugin"
        }
    getByName<org.shipkit.github.release.GithubReleaseTask>("githubRelease") {
        dependsOn(generateChangelog)
        repository = generateChangelog.repository
        changelog = generateChangelog.outputFile
        githubToken = System.getenv("GITHUB_TOKEN")
        newTagRevision = System.getenv("GITHUB_SHA")
    }
}

repositories {
    gradlePluginPortal() // other plugins on the plugin portal
    mavenCentral() // general dependencies
}

dependencies {
    // kotlin reflection
    implementation(kotlin("reflect"))

    // kotlin gradle plugin
    implementation(kotlin("gradle-plugin"))

    // nebula plugins
    implementation("com.netflix.nebula:nebula-project-plugin:_")

    // dokka plugins
    implementation("org.jetbrains.dokka:dokka-core:_")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:_")

    // shipkit
    implementation("org.shipkit:shipkit-auto-version:_")
    implementation("org.shipkit:shipkit-changelog:_")

    // test logger plugin
    implementation("com.adarshr:gradle-test-logger-plugin:_")

    // detekt plugin
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:_")

    // ktlint plugin
    implementation("org.jlleitschuh.gradle:ktlint-gradle:_")

    // gradle nexus publish plugin
    implementation("io.github.gradle-nexus:publish-plugin:_")

    // github api
    implementation("org.kohsuke:github-api:_")
}

project.ext.set("gradle.publish.key", System.getenv("GRADLE_PUBLISH_KEY"))
project.ext.set("gradle.publish.secret", System.getenv("GRADLE_PUBLISH_SECRET"))
