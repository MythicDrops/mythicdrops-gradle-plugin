package dev.mythicdrops.gradle.projects

import dev.mythicdrops.gradle.release.MythicDropsReleaseGitHubPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * Plugin that enables automatic versioning via ShipKit.
 */
open class MythicDropsRootPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (target != target.rootProject) {
            // do nothing if we're not being applied to the root project
            return
        }

        target.pluginManager.apply("org.shipkit.shipkit-auto-version")
        target.pluginManager.apply(MythicDropsReleaseGitHubPlugin::class)
    }
}
