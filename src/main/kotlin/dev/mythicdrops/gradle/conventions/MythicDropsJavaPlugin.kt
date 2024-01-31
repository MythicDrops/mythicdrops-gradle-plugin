package dev.mythicdrops.gradle.conventions

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPlugin
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.tasks.JacocoReport

val DEFAULT_JAVA_VERSION = JavaVersion.VERSION_17

/**
 * Plugin that configures Java for JDK 17 and enables JaCoCo.
 */
open class MythicDropsJavaPlugin : DependentPlugin("Java", "java") {
    override fun configureProject(target: Project) {
        val javaExtension =
            target.extensions.create<MythicDropsJavaExtension>("mythicDropsJava").apply {
                // default to Java 17
                javaVersion.convention(DEFAULT_JAVA_VERSION)
            }

        target.configure<JavaPluginExtension> {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(javaExtension.javaVersion.get().majorVersion))
            }
        }

        // enable and configure JaCoCo
        target.pluginManager.apply(JacocoPlugin::class.java)
        target.configure<JacocoPluginExtension> {
            toolVersion = "0.8.11"
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
