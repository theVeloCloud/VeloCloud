package de.snenjih.velocloud.platforms.tasks

import de.snenjih.velocloud.platforms.PlatformParameters
import de.snenjih.velocloud.platforms.ServerPlatformForwarding
import java.nio.file.Path

data class PlatformTask(val name: String, val steps: List<PlatformTaskStep>) {

    fun runTask(servicePath: Path, environment: PlatformParameters) {
        val forwarding = environment.getParameter<ServerPlatformForwarding>("forwarding")

        this.steps.forEach {
            if (it.forwardingFilter == null || it.forwardingFilter == forwarding) {
                it.run(servicePath, environment)
            }
        }
    }

}