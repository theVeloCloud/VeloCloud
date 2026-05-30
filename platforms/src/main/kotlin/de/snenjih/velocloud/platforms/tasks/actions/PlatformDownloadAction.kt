package de.snenjih.velocloud.platforms.tasks.actions

import de.snenjih.velocloud.platforms.PlatformParameters
import de.snenjih.velocloud.platforms.tasks.PlatformTaskStep
import java.io.BufferedInputStream
import java.net.URI
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.outputStream

class PlatformDownloadAction(val url: String) : PlatformAction() {
    override fun run(
        file: Path,
        step: PlatformTaskStep,
        environment: PlatformParameters
    ) {
        val processedUrl = environment.modifyValueWithEnvironment(url)
        file.createParentDirectories()

        URI(processedUrl).toURL().openStream().use { input ->
            BufferedInputStream(input).use { buffered ->
                file.outputStream().use { output ->
                    buffered.copyTo(output)
                }
            }
        }
    }
}