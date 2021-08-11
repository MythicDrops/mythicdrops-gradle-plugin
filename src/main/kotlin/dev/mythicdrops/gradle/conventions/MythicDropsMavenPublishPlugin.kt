package dev.mythicdrops.gradle.conventions

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin

/**
 * Plugin that configures Maven publishing to include compileOnly dependencies as "provided" and have an MIT license.
 * Also configures publishing to Sonatype Nexus.
 */
open class MythicDropsMavenPublishPlugin : DependentPlugin("Maven Publish", "maven-publish") {
    override fun configureProject(target: Project) {
        target.configure<PublishingExtension> {
            publications.withType<MavenPublication> {
                // POM contents for Maven Central
                pom {
                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://opensource.org/licenses/MIT")
                            distribution.set("repo")
                        }
                    }
                    pom.addCompileOnlyDependenciesAsProvided(target)
                }
            }
        }

        // gather signing parameters
        val signingParams = SigningNullableParams().toSigningParams() ?: return

        target.pluginManager.apply(SigningPlugin::class)
        target.configure<SigningExtension> {
            // Uses ASCII-armored keys (typically provided on GitHub Actions)
            useInMemoryPgpKeys(signingParams.pgpKey, signingParams.pgpPwd)
            // Always sign Maven publications in case they go to Maven Central
            sign(target.extensions.getByType<PublishingExtension>().publications.matching { it is MavenPublication })
        }
    }

    /**
     * Adds any `compileOnly` Gradle dependencies to a generated POM as `provided` Maven dependency.
     */
    private fun MavenPom.addCompileOnlyDependenciesAsProvided(project: Project) {
        withXml {
            val root = asNode()
            // we only add compileOnly dependencies if the configuration even exists
            val dependencies = project.configurations.findByName("compileOnly")?.dependencies ?: return@withXml
            if (dependencies.size > 0) {
                val deps = root.children().find {
                    it is groovy.util.Node && it.name().toString()
                        .endsWith("dependencies")
                } as groovy.util.Node? ?: root.appendNode("dependencies")
                dependencies.forEach { dependency ->
                    deps.appendNode("dependency").apply {
                        appendNode("groupId", dependency.group)
                        appendNode("artifactId", dependency.name)
                        appendNode("version", dependency.version)
                        appendNode("scope", "provided")
                    }
                }
            }
        }
    }

    private data class SigningParams(
        val pgpKey: String,
        val pgpPwd: String
    )

    /**
     * Gets the nullable params needed for signing and configuring Sonatype.
     */
    private data class SigningNullableParams(
        val pgpKey: String? = System.getenv("PGP_KEY"),
        val pgpPwd: String? = System.getenv("PGP_PWD")
    ) {
        /**
         * Converts to a non-nullable version of the params, returning null if any values are null.
         */
        fun toSigningParams(): SigningParams? = when {
            pgpKey == null -> null
            pgpPwd == null -> null
            else -> SigningParams(pgpKey, pgpPwd)
        }
    }
}
