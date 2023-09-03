package dev.mythicdrops.gradle.projects

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.TestLoggerPlugin
import com.adarshr.gradle.testlogger.theme.ThemeType
import nebula.plugin.responsible.NebulaResponsiblePlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

/**
 * Plugin that applies sensible defaults to all projects. Intended for use on root and subprojects.
 */
open class MythicDropsBasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // default project description to the root project description if not provided
        target.description = target.description ?: target.rootProject.description

        // apply the nebula responsible plugin for base defaults
        target.pluginManager.apply(NebulaResponsiblePlugin::class.java)

        // apply the test logger plugin to make test logs easy to read
        target.pluginManager.apply(TestLoggerPlugin::class.java)
        target.configure<TestLoggerExtension> {
            theme = ThemeType.MOCHA
            showSimpleNames = true
            showStandardStreams = true
            showFailedStandardStreams = true
            showSkippedStandardStreams = false
            showPassedStandardStreams = false
        }
    }
}
