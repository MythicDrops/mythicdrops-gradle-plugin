package dev.mythicdrops.gradle.release

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getByName
import org.gradle.kotlin.dsl.withType
import org.shipkit.changelog.GenerateChangelogTask
import org.shipkit.github.release.GithubReleaseTask

/**
 * Plugin that configures a root project for releasing to GitHub via GitHub actions.
 */
open class MythicDropsReleaseGitHubPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (target != target.rootProject) {
            // do nothing if we're not being applied to the root project
            return
        }

        // create an extension for configuration purposes
        val githubReleaseExtension = target.extensions.create<MythicDropsReleaseGitHubExtension>("mythicDropsRelease")

        // add release plugins
        target.pluginManager.apply("org.shipkit.shipkit-changelog")
        target.pluginManager.apply("org.shipkit.shipkit-github-release")

        // configure tasks for creating a GitHub release
        target.tasks.register("githubReleaseAssetUpload", MythicDropsGitHubReleaseAssetUploadTask::class.java) {
            githubApiUrl.set("https://api.github.com")
            releaseTag.set("v${target.version}")
            repository.set(githubReleaseExtension.repository)
            githubToken.set(System.getenv("GITHUB_TOKEN"))
            assets.from(githubReleaseExtension.assets)
        }
        target.tasks.withType<GenerateChangelogTask>().configureEach {
            previousRevision = target.extra.get("shipkit-auto-version.previous-tag")?.toString()
            githubToken = System.getenv("GITHUB_TOKEN")
            repository = githubReleaseExtension.repository.get()
        }
        target.tasks.withType<GithubReleaseTask>().configureEach {
            dependsOn(target.tasks.getByName("generateChangelog"))
            finalizedBy(target.tasks.getByName("githubReleaseAssetUpload"))
            repository = githubReleaseExtension.repository.get()
            changelog = target.tasks.getByName<GenerateChangelogTask>("generateChangelog").outputFile
            githubToken = System.getenv("GITHUB_TOKEN")
            newTagRevision = System.getenv("GITHUB_SHA")
        }
    }
}
