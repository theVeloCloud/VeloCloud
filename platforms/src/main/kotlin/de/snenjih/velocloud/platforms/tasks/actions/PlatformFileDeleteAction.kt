package de.snenjih.velocloud.platforms.tasks.actions

import de.snenjih.velocloud.platforms.PlatformParameters
import de.snenjih.velocloud.platforms.tasks.PlatformTaskStep
import java.nio.file.Path
import kotlin.io.path.deleteIfExists

class PlatformFileDeleteAction : PlatformAction() {
    override fun run(
        file: Path,
        step: PlatformTaskStep,
        environment: PlatformParameters
    ) {
        file.deleteIfExists()
    }
}