package de.snenjih.velocloud.platforms.tasks.actions

import de.snenjih.velocloud.platforms.PlatformParameters
import de.snenjih.velocloud.platforms.tasks.PlatformTaskStep
import java.io.File
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

class PlatformDirectoryDeleteAction : PlatformAction() {
    override fun run(
        file: Path,
        step: PlatformTaskStep,
        environment: PlatformParameters
    ) {
        if (file.exists() && file.isDirectory()) {
            deleteDirectory(file.toFile())
        }
    }

    private fun deleteDirectory(directory: File) {
        if (directory.exists() && directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                if (file.isDirectory) {
                    deleteDirectory(file)
                } else {
                    file.delete()
                }
            }
            directory.delete()
        }
    }
}