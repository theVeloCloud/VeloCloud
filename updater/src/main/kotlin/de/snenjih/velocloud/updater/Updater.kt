package de.snenjih.velocloud.updater

import de.snenjih.velocloud.common.os.OS
import de.snenjih.velocloud.common.os.currentOS
import de.snenjih.velocloud.common.version.velocloudVersion
import java.io.File
import java.util.LinkedList

object Updater {

    private var syncGitHubVersion = false
    private val versions = LinkedList<String>()

    init {
        this.tryUpdate()
    }

    fun latestVersion(): String {
        return versions.first()
    }

    fun newVersionAvailable(): Boolean {
        return velocloudVersion() != latestVersion() && versions.contains(velocloudVersion())
    }

    fun hasSyncGitHubVersion() = syncGitHubVersion

    fun availableVersions(): List<String> {
        return versions
    }

    fun update(version: String = latestVersion()) {
        println("Launching updater...")

        val jarName = "velocloud-updater-${velocloudVersion()}.jar"

        val processBuilder = when (currentOS) {
            OS.WIN -> ProcessBuilder(
                "cmd.exe",
                "/c",
                "start",
                "cmd.exe",
                "/c",
                "java",
                "-jar",
                jarName,
                "--version=$version"
            )

            else -> ProcessBuilder("java", "-jar", jarName, "--version=$version")
        }

        processBuilder.environment()["VELOCLOUD_VERSION"] = velocloudVersion()
        processBuilder.directory(File("local/libs"))
        processBuilder.inheritIO()

        processBuilder.start()
    }

    fun tryUpdate() {
        this.versions.clear()
        this.versions += readTags()

        if (!versions.isEmpty()) {
            syncGitHubVersion = true
        } else {
            versions += velocloudVersion()
        }
    }
}