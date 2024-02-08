package dev.mythicdrops.gradle.spigot

import io.github.patrick.gradle.remapper.RemapTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerIfAbsent

class MythicDropsSpigotBuildToolsGradlePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val extension =
            target.extensions.create<MythicDropsSpigotBuildToolsExtension>("spigotBuildTools").apply {
                buildToolsLocation.convention(
                    target.layout.buildDirectory.file("spigot-build-tools/BuildTools.jar"),
                )
                includeRemapped.convention(true)
                version.convention("")
            }

        // Register sync task
        target.gradle.sharedServices.registerIfAbsent(SyncTaskBuildService.NAME, SyncTaskBuildService::class) {
            // throttle the usages of the build tools
            maxParallelUsages.set(1)
        }

        val downloadTask =
            target.tasks.create<DownloadSpigotBuildToolsTask>("downloadSpigotBuildTools") {
                downloadedJar.set(extension.buildToolsLocation)
            }
        target.tasks.register<RunSpigotBuildToolsTask>("runSpigotBuildTools") {
            buildToolsLocation.set(extension.buildToolsLocation)
            includeRemapped.set(extension.includeRemapped)
            version.set(extension.version)
            dependsOn(downloadTask)
        }

        target.plugins.apply("io.github.patrick.remapper")
        target.tasks.named<RemapTask>("remap") {
            version.set(extension.version)
            dependsOn("jar")
        }
        target.tasks.named<Jar>("jar") {
            finalizedBy("remap")
        }
    }
}
