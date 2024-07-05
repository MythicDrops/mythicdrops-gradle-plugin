package dev.mythicdrops.gradle.conventions

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

const val DEFAULT_JAVA_VERSION = 21

/**
 * Plugin that configures Java for JDK 17 and enables JaCoCo.
 */
open class MythicDropsJavaPlugin : DependentPlugin("Java", "java") {
    override fun configureProject(target: Project) {
        target.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.convention(JavaLanguageVersion.of(DEFAULT_JAVA_VERSION))
            }
        }

        // enable and configure JaCoCo
        target.pluginManager.apply(JacocoPlugin::class.java)
        target.configure<JacocoPluginExtension> {
            toolVersion = "0.8.12"
        }
        target.tasks.withType<JacocoReport> {
            reports {
                xml.required.set(true)
            }
        }
        target.tasks.withType<Test> {
            finalizedBy("jacocoTestReport")
        }
    }
}
