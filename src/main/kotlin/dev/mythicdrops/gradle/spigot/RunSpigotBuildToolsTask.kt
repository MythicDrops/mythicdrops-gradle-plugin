package dev.mythicdrops.gradle.spigot

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecOperations
import java.io.File
import java.nio.file.Paths
import javax.inject.Inject

abstract class RunSpigotBuildToolsTask
    @Inject
    constructor(
        private val execOperations: ExecOperations,
        private val fileSystemOperations: FileSystemOperations,
    ) : DefaultTask() {
        @get:ServiceReference(SyncTaskBuildService.NAME)
        abstract val syncTask: Property<SyncTaskBuildService>

        @get:InputFile
        abstract val buildToolsLocation: RegularFileProperty

        @get:Input
        abstract val includeRemapped: Property<Boolean>

        @get:Input
        abstract val version: Property<String>

        init {
            description = "Runs Spigot BuildTools.jar for a specific Minecraft version"
            group = "spigot"
        }

        @TaskAction
        fun runSpigotBuildTools() {
            val version = version.getOrElse("")
            if (version.isBlank()) {
                logger.lifecycle("Not running Spigot build tools as the version is blank")
                return
            }

            val mavenLocalDirectory = Paths.get(project.repositories.mavenLocal().url).toFile()
            if (!mavenLocalDirectory.exists()) {
                logger.lifecycle("Creating Maven Local repository at ${mavenLocalDirectory.absolutePath}")
                mavenLocalDirectory.mkdirs()
            }

            normalVersion(mavenLocalDirectory, version)
            if (includeRemapped.getOrElse(false)) {
                remappedVersion(mavenLocalDirectory, version)
            }
        }

        private fun normalVersion(
            mavenLocalDirectory: File,
            version: String,
        ) {
            val versionJar =
                mavenLocalDirectory.resolve(
                    "org/spigotmc/spigot/$version-R0.1-SNAPSHOT/spigot-$version-R0.1-SNAPSHOT.jar",
                )
            if (versionJar.exists()) {
                logger.lifecycle("Skipping $version as Spigot JAR is found at ${versionJar.absolutePath}")
                return
            }
            val jar = buildToolsLocation.get().asFile
            val versionDir = jar.parentFile.resolve(version)
            fileSystemOperations.copy {
                from(jar)
                into(versionDir)
            }
            execOperations.javaexec {
                args(listOf("--rev", version))
                workingDir = versionDir.absoluteFile
                jvmArgs = listOf("-Xmx1024M")
                classpath(buildToolsLocation)
            }
        }

        private fun remappedVersion(
            mavenLocalDirectory: File,
            version: String,
        ) {
            val versionJar =
                mavenLocalDirectory.resolve(
                    "org/spigotmc/spigot/$version-R0.1-SNAPSHOT/spigot-$version-R0.1-SNAPSHOT-remapped-mojang.jar",
                )
            if (versionJar.exists()) {
                logger.lifecycle("Skipping $version as Spigot remapped JAR is found at ${versionJar.absolutePath}")
                return
            }
            val jar = buildToolsLocation.get().asFile
            val versionDir = jar.parentFile.resolve(version)
            fileSystemOperations.copy {
                from(jar)
                into(versionDir)
            }
            execOperations.javaexec {
                args(listOf("--rev", version, "--remapped"))
                workingDir = versionDir.absoluteFile
                jvmArgs = listOf("-Xmx1024M")
                classpath(buildToolsLocation)
            }
        }
    }
