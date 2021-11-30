package dev.mythicdrops.gradle.conventions

/**
 * Configuration for selecting which version of Java to compile for.
 *
 * @property javaVersion Version of Java to compile for (e.g., Java 16/17)
 */
open class MythicDropsJavaExtension {
    var javaVersion: Int = 16
}
