
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Duration

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
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

gradlePlugin {
    plugins {
        create("mythicDropsProject") {
            id = "dev.mythicdrops.gradle.project"
            displayName = "mythicDropsGradleProject"
            description = "Orchestration plugin for all MythicDrops Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.MythicDropsPlugin"
        }
        create("mythicDropsProjectRoot") {
            id = "dev.mythicdrops.gradle.project.root"
            displayName = "mythicDropsGradleProjectRoot"
            description = "Common conventions for MythicDrops root Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.projects.MythicDropsRootPlugin"
        }
        create("mythicDropsProjectBase") {
            id = "dev.mythicdrops.gradle.project.base"
            displayName = "mythicDropsGradleProjectBase"
            description = "Common conventions for all MythicDrops Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.projects.MythicDropsBasePlugin"
        }
        create("mythicDropsReleaseGitHub") {
            id = "dev.mythicdrops.gradle.release.github"
            displayName = "mythicDropsGradleReleaseGitHub"
            description = "Create a GitHub Release via GitHub Actions for all MythicDrops Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.release.MythicDropsReleaseGitHubPlugin"
        }
        create("mythicDropsConventionJava") {
            id = "dev.mythicdrops.gradle.convention.java"
            displayName = "mythicDropsGradleConventionJava"
            description = "Common conventions for all MythicDrops Java Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.conventions.MythicDropsJavaPlugin"
        }
        create("mythicDropsConventionKotlinJvm") {
            id = "dev.mythicdrops.gradle.convention.kotlin.jvm"
            displayName = "mythicDropsGradleConventionKotlinJvm"
            description = "Common conventions for all MythicDrops Kotlin JVM Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.conventions.MythicDropsKotlinJvmPlugin"
        }
        create("mythicDropsConventionsMavenPublish") {
            id = "dev.mythicdrops.gradle.convention.maven-publish"
            displayName = "mythicDropsGradle"
            description = "Common conventions for all MythicDrops Maven Publishing Gradle projects."
            implementationClass = "dev.mythicdrops.gradle.conventions.MythicDropsMavenPublishPlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/MythicDrops/mythicdrops-gradle-plugin"
    vcsUrl = "https://github.com/MythicDrops/mythicdrops-gradle-plugin"
    tags = listOf("kotlin", "pixeloutlaw", "convention")
}

// Separate integration tests from "fast" tests
val intTest: SourceSet by sourceSets.creating {
    compileClasspath += sourceSets.main.get().output + configurations.testRuntime.get()
    runtimeClasspath += output + compileClasspath
}
val intTestImplementation: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
val intTestRuntimeOnly: Configuration by configurations.getting {
    extendsFrom(configurations.testImplementation.get())
}
val integrationTest by tasks.registering(Test::class) {
    description = "Runs the functional tests"
    group = JavaBasePlugin.VERIFICATION_GROUP

    testClassesDirs = intTest.output.classesDirs
    classpath = intTest.runtimeClasspath
    shouldRunAfter(tasks.test)

    reports {
        html.outputLocation.set(file("${html.outputLocation.get()}/functional"))
        junitXml.outputLocation.set(file("${junitXml.outputLocation.get()}/functional"))
    }

    timeout.set(Duration.ofMinutes(2))
}

tasks {
    // get dokkaJavadoc task and make javadocJar depend on it
    val dokkaJavadoc by this
    getByName<Jar>("javadocJar") {
        dependsOn(dokkaJavadoc)
        from(dokkaJavadoc)
    }

    // compile targeting JDK8
    withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "1.8"
    }

    // use JUnit Jupiter
    withType<Test>() {
        useJUnitPlatform()
    }

    // check depends on integration tests to run
    check {
        dependsOn(integrationTest.get())
    }
}

repositories {
    gradlePluginPortal() // other plugins on the plugin portal
    mavenCentral() // general dependencies
}

dependencies {
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
    implementation("org.shipkit:shipkit-auto-version:_")

    // test logger plugin
    implementation("com.adarshr:gradle-test-logger-plugin:_")

    // detekt plugin
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:_")

    // ktlint plugin
    implementation("org.jlleitschuh.gradle:ktlint-gradle:_")

    // gradle nexus publish plugin
    implementation("io.github.gradle-nexus:publish-plugin:_")
}

val generateChangelog = tasks.getByName<org.shipkit.changelog.GenerateChangelogTask>("generateChangelog") {
    previousRevision = project.ext.get("shipkit-auto-version.previous-tag")?.toString()
    githubToken = System.getenv("GITHUB_TOKEN")
    repository = "MythicDrops/mythicdrops-gradle-plugin"
}

tasks.getByName<org.shipkit.github.release.GithubReleaseTask>("githubRelease") {
    dependsOn(generateChangelog)
    repository = generateChangelog.repository
    changelog = generateChangelog.outputFile
    githubToken = System.getenv("GITHUB_TOKEN")
    newTagRevision = System.getenv("GITHUB_SHA")
}

project.ext.set("gradle.publish.key", System.getenv("GRADLE_PUBLISH_KEY"))
project.ext.set("gradle.publish.secret", System.getenv("GRADLE_PUBLISH_SECRET"))
