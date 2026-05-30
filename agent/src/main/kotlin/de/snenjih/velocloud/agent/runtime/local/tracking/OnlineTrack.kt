package de.snenjih.velocloud.agent.runtime.local.tracking

import de.snenjih.velocloud.agent.runtime.abstracts.Track
import de.snenjih.velocloud.agent.runtime.local.LocalService

class OnlineTrack(private val localService: LocalService) : Track() {

    override fun start() {
        this.threads.add(Thread.startVirtualThread {
            try {
                localService.process?.waitFor()
            }catch (_: InterruptedException) {
                // we can ignore this exception; it means the process was interrupted
            }

            // When the process ends -> we can detect it here
            localService.shutdown()
        })
    }
}