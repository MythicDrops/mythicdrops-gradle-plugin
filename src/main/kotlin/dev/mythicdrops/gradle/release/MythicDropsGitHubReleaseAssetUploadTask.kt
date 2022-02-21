package dev.mythicdrops.gradle.release

import org.gradle.api.DefaultTask
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.TaskAction
import org.kohsuke.github.GitHubBuilder
import java.io.File

abstract class MythicDropsGitHubReleaseAssetUploadTask : DefaultTask() {
    companion object {
        private val LOG: Logger = Logging.getLogger(MythicDropsGitHubReleaseAssetUploadTask::class.java)
    }

    @get:InputFile
    abstract val asset: File

    @get:Input
    abstract val contentType: Property<String>

    @get:Input
    abstract val githubApiUrl: Property<String>

    @get:Input
    abstract val githubToken: Property<String>

    @get:Input
    abstract val releaseTag: Property<String>

    @get:Input
    abstract val repository: Property<String>

    @TaskAction
    open fun uploadGitHubReleaseAsset() {
        val github = GitHubBuilder().withOAuthToken(githubToken.get()).build()
        val githubRepository = github.getRepository(repository.get())
            ?: throw IllegalArgumentException("${repository.get()} does not exist")
        val githubRelease = githubRepository.getReleaseByTagName(releaseTag.get())
            ?: throw IllegalArgumentException("Release by tag name ${releaseTag.get()} does not exist")
        val githubAsset = githubRelease.uploadAsset(asset, contentType.get())
        LOG.lifecycle("Asset uploaded to GitHub release. You can download it at: ${githubAsset.browserDownloadUrl}")
    }
}
