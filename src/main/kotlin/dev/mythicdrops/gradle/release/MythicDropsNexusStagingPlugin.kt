package dev.mythicdrops.gradle.release

import io.github.gradlenexus.publishplugin.NexusPublishExtension
import io.github.gradlenexus.publishplugin.NexusPublishPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure

open class MythicDropsNexusStagingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        if (target != target.rootProject) {
            // do nothing as we're not the root project
            return
        }

        val sonatypeParams = SonatypeNullableParams().toSonatypeParams()

        enableSonatypePublishing(sonatypeParams, target)
    }

    private fun enableSonatypePublishing(
        sonatypeParams: SonatypeParams?,
        target: Project
    ) {
        if (sonatypeParams == null) {
            // if any environment variables are null, do not enable Sonatype publishing or signing
            return
        }

        target.pluginManager.apply(NexusPublishPlugin::class)
        target.configure<NexusPublishExtension> {
            repositories {
                sonatype {
                    username.set(sonatypeParams.sonatypeUser)
                    password.set(sonatypeParams.sonatypePwd)
                }
            }
        }
    }

    private data class SonatypeParams(
        val sonatypeUser: String,
        val sonatypePwd: String
    )

    /**
     * Gets the nullable params needed for signing and configuring Sonatype.
     */
    private data class SonatypeNullableParams(
        val sonatypeUser: String? = System.getenv("SONATYPE_USER"),
        val sonatypePwd: String? = System.getenv("SONATYPE_PWD")
    ) {
        /**
         * Converts to a non-nullable version of the params, returning null if any values are null.
         */
        fun toSonatypeParams(): SonatypeParams? = when {
            sonatypeUser == null -> null
            sonatypePwd == null -> null
            else -> SonatypeParams(sonatypeUser, sonatypePwd)
        }
    }
}
