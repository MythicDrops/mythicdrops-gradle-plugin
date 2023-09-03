package dev.mythicdrops.gradle.conventions

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

/**
 * Plugin that configures Maven publications for Java Platforms.
 */
open class MythicDropsJavaPlatformPlugin : DependentPlugin("Java Platform", "java-platform") {
    override fun configureProject(target: Project) {
        target.pluginManager.withPlugin("maven-publish") {
            target.extensions.getByType<PublishingExtension>().publications.withType<MavenPublication> {
                from(target.components.getByName("javaPlatform"))
            }
        }
    }
}
