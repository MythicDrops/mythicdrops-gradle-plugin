package dev.mythicdrops.gradle.conventions

import org.gradle.api.Project
import org.gradle.api.plugins.jvm.JvmTestSuite
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.base.TestingExtension

/**
 * Plugin that configures the base JVM Test Suite to use JUnit Jupiter.
 */
open class MythicDropsJvmTestSuitePlugin : DependentPlugin("JVM Test Suite", "org.gradle.jvm-test-suite") {
    @Suppress("UnstableApiUsage")
    override fun configureProject(target: Project) {
        target.configure<TestingExtension> {
            suites.withType<JvmTestSuite> {
                useJUnitJupiter()
            }
        }
    }
}
