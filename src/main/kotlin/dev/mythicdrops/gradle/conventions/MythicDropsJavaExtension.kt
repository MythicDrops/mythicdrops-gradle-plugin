package dev.mythicdrops.gradle.conventions

import org.gradle.api.JavaVersion
import org.gradle.api.provider.Property

/**
 * Configuration for selecting which version of Java to compile for.
 *
 * @property javaVersion Version of Java to compile for (e.g., Java 16/17)
 */
interface MythicDropsJavaExtension {
    val javaVersion: Property<JavaVersion>
}
