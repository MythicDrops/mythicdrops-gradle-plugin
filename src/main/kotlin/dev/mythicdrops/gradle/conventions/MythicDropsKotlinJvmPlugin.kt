package dev.mythicdrops.gradle.conventions

import io.gitlab.arturbosch.detekt.DetektPlugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

/**
 * Plugin that configures Kotlin for JDK 17, enables Detekt, and enables/configures KTLint to use 0.48.1.
 */
open class MythicDropsKotlinJvmPlugin : DependentPlugin("Kotlin JVM", "org.jetbrains.kotlin.jvm") {
    override fun configureProject(target: Project) {
        val javaExtension =
            target.extensions.getByType<MythicDropsJavaExtension>().apply {
                javaVersion.convention(DEFAULT_JAVA_VERSION)
            }

        // apply plugins
        target.pluginManager.apply(DetektPlugin::class.java)
        target.pluginManager.apply(DokkaPlugin::class.java)
        target.pluginManager.apply(KtlintPlugin::class.java)

        target.configure<KotlinJvmProjectExtension> {
            jvmToolchain {
                languageVersion.set(JavaLanguageVersion.of(javaExtension.javaVersion.get().majorVersion))
            }
        }

        // exclude files from the build directory from being linted or formatted
        target.configure<KtlintExtension> {
            // ktlint version that supports kotlin 1.8.0
            version.set("0.48.1")
            filter {
                exclude("**/build/**")
            }
        }

        target.tasks.withType<KotlinCompile> {
            kotlinOptions {
                javaParameters = true
            }
        }

        // make javadoc jar rely on dokka
        val dokkaJavadoc = target.tasks.getByName<DokkaTask>("dokkaJavadoc")
        target.tasks.getByName("javadocJar", Jar::class) {
            dependsOn(dokkaJavadoc)
            from(dokkaJavadoc)
        }
    }
}
