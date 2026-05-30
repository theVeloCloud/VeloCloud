package de.snenjih.velocloud.agent.runtime.docker

import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.runtime.Runtime
import de.snenjih.velocloud.agent.runtime.RuntimeLoader
import java.nio.file.Files
import java.nio.file.Paths

class DockerRuntimeLoader : RuntimeLoader {

    override fun runnable(): Boolean {
        return try {
            return Files.exists(Paths.get("/.dockerenv")) || Files.exists(Paths.get("/run/.containerenv"))
        } catch (e: Exception) {
            i18n.debug("agent.runtime.docker.connection.failed", e.javaClass.simpleName, e.message)
            false
        }
    }

    override fun instance(): Runtime {
        return DockerRuntime()
    }
}