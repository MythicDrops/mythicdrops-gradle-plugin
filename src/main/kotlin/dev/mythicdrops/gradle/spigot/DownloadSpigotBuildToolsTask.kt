package dev.mythicdrops.gradle.spigot

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.withGroovyBuilder

/**
 * Task to download Spigot Build Tools to the build directory.
 */
abstract class DownloadSpigotBuildToolsTask : DefaultTask() {
    @get:Input
    abstract val downloadUrl: Property<String>

    @get:OutputFile
    abstract val downloadedJar: RegularFileProperty

    init {
        description = "Download Spigot BuildTools.jar into a build directory for use."
        group = "spigot"
        downloadUrl.convention(
            "https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar",
        )
    }

    @TaskAction
    fun download() {
        ant.withGroovyBuilder {
            "get"("src" to downloadUrl.get(), "dest" to downloadedJar.get().asFile.toPath())
        }
    }
}
