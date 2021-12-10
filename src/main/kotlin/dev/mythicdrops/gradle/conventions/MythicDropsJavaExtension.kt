package dev.mythicdrops.gradle.conventions

import org.gradle.api.JavaVersion

/**
 * Configuration for selecting which version of Java to compile for.
 *
 * @property javaVersion Version of Java to compile for (e.g., Java 16/17)
 */
open class MythicDropsJavaExtension {
    var javaVersion: String = JavaVersion.VERSION_16.majorVersion
}
