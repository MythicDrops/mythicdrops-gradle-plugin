package dev.mythicdrops.gradle.release

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.kohsuke.github.GitHubBuilder
import java.nio.file.Files

abstract class MythicDropsGitHubReleaseAssetUploadTask : DefaultTask() {
    companion object {
        private val LOG: Logger = Logging.getLogger(MythicDropsGitHubReleaseAssetUploadTask::class.java)
    }

    @get:InputFiles
    abstract val assets: ConfigurableFileCollection

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
        if (assets.isEmpty) {
            LOG.lifecycle("Not uploading assets to GitHub as none have been configured")
            return
        }

        val github = GitHubBuilder().withOAuthToken(githubToken.get()).build()
        val githubRepository = github.getRepository(repository.get())
            ?: throw IllegalArgumentException("${repository.get()} does not exist")
        val githubRelease = githubRepository.getReleaseByTagName(releaseTag.get())
            ?: throw IllegalArgumentException("Release by tag name ${releaseTag.get()} does not exist")

        assets.forEach {
            val contentType = Files.probeContentType(it.toPath())
            val githubAsset = githubRelease.uploadAsset(it, contentType)
            LOG.lifecycle("Asset uploaded to GitHub release. You can download it at: ${githubAsset.browserDownloadUrl}")
        }
    }
}
