package dev.mythicdrops.gradle.conventions

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel

/**
 * Base class for a Gradle plugin that requires another plugin applied to the given project in order to be applied.
 *
 * @property pluginDescription Description of the configuring plugin (e.g., "MythicDrops Java Plugin")
 * @property prerequisitePluginId Gradle Plugin ID of plugin that must be applied (e.g., "java")
 */
abstract class DependentPlugin(
    private val pluginDescription: String,
    private val prerequisitePluginId: String
) : Plugin<Project> {
    override fun apply(target: Project) {
        if (!target.pluginManager.hasPlugin(prerequisitePluginId)) {
            target.logger.log(
                LogLevel.QUIET,
                "Not applying MythicDrops {} plugin as the \"{}\" plugin isn't applied",
                pluginDescription,
                prerequisitePluginId
            )
            return
        }
        configureProject(target)
    }

    abstract fun configureProject(target: Project)
}
