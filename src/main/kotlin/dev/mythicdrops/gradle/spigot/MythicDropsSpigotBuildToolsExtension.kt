package dev.mythicdrops.gradle.spigot

import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

interface MythicDropsSpigotBuildToolsExtension {
    /**
     * Location of the BuildTools.jar
     */
    val buildToolsLocation: RegularFileProperty

    /**
     * Should a version with `--remapped` also be run?
     */
    val includeRemapped: Property<Boolean>

    /**
     * Which version of Spigot to build and publish into [mavenLocal()].
     */
    val version: Property<String>
}
