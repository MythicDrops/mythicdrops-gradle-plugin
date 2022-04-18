package dev.mythicdrops.gradle

import dev.mythicdrops.gradle.conventions.MythicDropsJavaPlugin
import dev.mythicdrops.gradle.conventions.MythicDropsKotlinJvmPlugin
import dev.mythicdrops.gradle.conventions.MythicDropsMavenPublishPlugin
import dev.mythicdrops.gradle.projects.MythicDropsBasePlugin
import dev.mythicdrops.gradle.projects.MythicDropsRootPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply

/**
 * Plugin that orchestrates and applies all the other plugins.
 */
open class MythicDropsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // don't do anything if we aren't the root project
        if (target != target.rootProject) {
            return
        }

        // apply root project configuration
        target.pluginManager.apply(MythicDropsRootPlugin::class)

        // the other plugins we orchestrate are all able to go on `allprojects`
        target.allprojects {
            pluginManager.apply(MythicDropsBasePlugin::class)
            pluginManager.apply(MythicDropsJavaPlugin::class)
            pluginManager.apply(MythicDropsKotlinJvmPlugin::class)
            pluginManager.apply(MythicDropsMavenPublishPlugin::class)
        }
    }
}
