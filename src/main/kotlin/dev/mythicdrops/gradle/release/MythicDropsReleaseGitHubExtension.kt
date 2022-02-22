package dev.mythicdrops.gradle.release

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

/**
 * Configuration for creating a GitHub release for MythicDrops Gradle projects.
 *
 * @property repository GitHub repository for the project (e.g., MythicDrops/kindling)
 * @property assets Any assets to upload to GitHub on release
 */
interface MythicDropsReleaseGitHubExtension {
    val repository: Property<String>

    val assets: ConfigurableFileCollection
}
