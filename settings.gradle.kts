pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    // See https://jmfayard.github.io/refreshVersions
    id("de.fayard.refreshVersions") version "0.60.5"
    id("com.gradle.develocity") version "3.17.5"
////                        # available:"3.17.6"
////                        # available:"3.18"
////                        # available:"3.18.1"
////                        # available:"3.18.2"
////                        # available:"3.19"
////                        # available:"3.19.1"
////                        # available:"3.19.2"
////                        # available:"4.0"
}

develocity {
    buildScan {
        publishing.onlyIf { it.buildResult.failures.isNotEmpty() && !System.getenv("CI").isNullOrEmpty() }
        termsOfUseUrl = "https://gradle.com/terms-of-service"
        termsOfUseAgree = "yes"
    }
}
