package dev.mythicdrops.gradle.conventions

import io.gitlab.arturbosch.detekt.DetektPlugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainSpec
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
 * Plugin that configures Kotlin for JDK 16, enables Detekt, and enables/configures KTLint to use 0.45.2.
 */
open class MythicDropsKotlinJvmPlugin : DependentPlugin("Kotlin JVM", "org.jetbrains.kotlin.jvm") {
    override fun configureProject(target: Project) {
        val javaExtension = target.extensions.getByType<MythicDropsJavaExtension>()

        // apply plugins
        target.pluginManager.apply(DetektPlugin::class.java)
        target.pluginManager.apply(DokkaPlugin::class.java)
        target.pluginManager.apply(KtlintPlugin::class.java)

        // configure kotlin to use JDK 16
        target.configure<KotlinJvmProjectExtension> {
            jvmToolchain {
                (this as JavaToolchainSpec).languageVersion
                    .set(JavaLanguageVersion.of(javaExtension.javaVersion.get().majorVersion))
            }
        }

        // set ktlint version to 0.45.2
        target.configure<KtlintExtension> {
            version.set("0.45.2")
            filter {
                exclude("**/generated/**")
                exclude("**/generated-sources/**")
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
