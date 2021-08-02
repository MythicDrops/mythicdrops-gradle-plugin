package dev.mythicdrops.gradle.release

/**
 * Configuration for creating a GitHub release for MythicDrops Gradle projects.
 *
 * @property repository GitHub repository for the project (e.g., MythicDrops/kindling)
 */
open class MythicDropsReleaseGitHubExtension {
    var repository: String = ""
}
