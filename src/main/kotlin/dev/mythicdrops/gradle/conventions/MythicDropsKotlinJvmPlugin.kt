package dev.mythicdrops.gradle.conventions

import io.gitlab.arturbosch.detekt.DetektPlugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.KtlintPlugin

/**
 * Plugin that configures Kotlin for JDK 17, enables Detekt, and enables/configures KTLint to use 1.1.1.
 */
open class MythicDropsKotlinJvmPlugin : DependentPlugin("Kotlin JVM", "org.jetbrains.kotlin.jvm") {
    override fun configureProject(target: Project) {
        // apply plugins
        target.pluginManager.apply(DetektPlugin::class.java)
        target.pluginManager.apply(DokkaPlugin::class.java)
        target.pluginManager.apply(KtlintPlugin::class.java)

        // exclude files from the build directory from being linted or formatted
        target.configure<KtlintExtension> {
            version.set("1.1.1")
            filter {
                exclude { entry ->
                    entry.file.toString().contains("generated")
                }
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
