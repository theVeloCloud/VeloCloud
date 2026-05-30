package de.snenjih.velocloud.platforms.tasks.actions

import de.snenjih.velocloud.common.os.currentOS
import de.snenjih.velocloud.platforms.PlatformParameters
import de.snenjih.velocloud.platforms.tasks.PlatformTaskStep
import java.nio.file.Path

class PlatformExecuteCommandAction(val command: String) : PlatformAction() {
    override fun run(
        file: Path,
        step: PlatformTaskStep,
        environment: PlatformParameters
    ) {
        val builder = ProcessBuilder()


        builder.command(*currentOS.shellPrefix, environment.modifyValueWithEnvironment(command))
        builder.directory(file.toFile())
        val process = builder.start()

        process.waitFor()
    }
}