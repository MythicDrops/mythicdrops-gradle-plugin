package dev.mythicdrops.gradle.spigot

import org.gradle.api.DefaultTask
import org.gradle.api.file.FileSystemOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.services.ServiceReference
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.TaskAction
import org.gradle.jvm.toolchain.JavaLauncher
import org.gradle.jvm.toolchain.JavaToolchainService
import org.gradle.kotlin.dsl.getByType
import org.gradle.process.ExecOperations
import java.io.File
import java.nio.file.Paths
import javax.inject.Inject

abstract class RunSpigotBuildToolsTask : DefaultTask() {
    @get:ServiceReference(SyncTaskBuildService.NAME)
    abstract val syncTask: Property<SyncTaskBuildService>

    @get:InputFile
    abstract val buildToolsLocation: RegularFileProperty

    @get:Input
    abstract val includeRemapped: Property<Boolean>

    @get:Input
    abstract val version: Property<String>

    @get:Nested
    abstract val launcher: Property<JavaLauncher>

    @get:Inject
    abstract val execOperations: ExecOperations

    @get:Inject
    abstract val fileSystemOperations: FileSystemOperations

    @get:Inject
    abstract val javaToolchainService: JavaToolchainService

    init {
        description = "Runs Spigot BuildTools.jar for a specific Minecraft version"
        group = "spigot"

        val toolchain = project.extensions.getByType<JavaPluginExtension>().toolchain
        val defaultLauncher = javaToolchainService.launcherFor(toolchain)
        launcher.convention(defaultLauncher)
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

        javaexecSpigotBuildTools(mavenLocalDirectory, version, false)
        if (includeRemapped.getOrElse(false)) {
            javaexecSpigotBuildTools(mavenLocalDirectory, version, true)
        }
    }

    private fun javaexecSpigotBuildTools(
        mavenLocalDirectory: File,
        version: String,
        isRemapped: Boolean,
    ) {
        val versionJar =
            if (isRemapped) {
                mavenLocalDirectory.resolve(
                    "org/spigotmc/spigot/$version-R0.1-SNAPSHOT/spigot-$version-R0.1-SNAPSHOT-remapped-mojang.jar",
                )
            } else {
                mavenLocalDirectory.resolve(
                    "org/spigotmc/spigot/$version-R0.1-SNAPSHOT/spigot-$version-R0.1-SNAPSHOT.jar",
                )
            }

        if (versionJar.exists()) {
            logger.lifecycle("Skipping $version as Spigot JAR is found at ${versionJar.absolutePath}")
            return
        }
        val jar = buildToolsLocation.get().asFile
        val versionDir = jar.parentFile.resolve(version)
        val args = listOfNotNull("--rev", version, if (isRemapped) "--remapped" else null)
        fileSystemOperations.copy {
            from(jar)
            into(versionDir)
        }
        execOperations.javaexec {
            args(args)
            workingDir = versionDir.absoluteFile
            jvmArgs = listOf("-Xmx1024M")
            classpath(buildToolsLocation)
            executable(launcher.get().executablePath)
        }
    }
}
