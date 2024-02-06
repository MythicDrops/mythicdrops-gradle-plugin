package dev.mythicdrops.gradle.spigot

import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

/**
 * Used for preventing multiple expensive tasks from being used simultaneously.
 */
abstract class SyncTaskBuildService : BuildService<SyncTaskBuildService.Params> {
    companion object {
        const val NAME = "mythicDropsSyncTask"
    }

    interface Params : BuildServiceParameters
}
