package de.snenjih.velocloud.platforms.tasks.actions

import de.snenjih.velocloud.platforms.PlatformParameters
import de.snenjih.velocloud.platforms.tasks.PlatformTaskStep
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories

class PlatformFileWriteAction(val content: String) : PlatformAction() {

    override fun run(
        file: Path,
        step: PlatformTaskStep,
        environment: PlatformParameters
    ) {
        file.parent.createDirectories()

        Files.writeString(file, environment.modifyValueWithEnvironment(content))
    }
}