package de.snenjih.velocloud.agent.runtime.docker

import de.snenjih.velocloud.agent.Agent

class DockerCloudInformationThread : Thread("velocloud-local-cloud-information") {

    private var lastCleanup: Long = 0L

    override fun run() {
        while (!isInterrupted) {
            val now = System.currentTimeMillis()
            Agent.cloudInformationStorage.saveCurrentCloudInformation()

            if (now - lastCleanup >= 5 * 60 * 1000) {
                Agent.cloudInformationStorage.cleanup(7L * 24 * 60 * 60 * 1000)
                lastCleanup = now
            }

            try {
                sleep(5000)
            } catch (_: InterruptedException) {
                interrupt()
                break
            }
        }
    }
}