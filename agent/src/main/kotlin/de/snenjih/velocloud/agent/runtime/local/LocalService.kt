package de.snenjih.velocloud.agent.runtime.local

import de.snenjih.velocloud.agent.groups.AbstractGroup
import de.snenjih.velocloud.agent.runtime.local.tracking.OnlineTrack
import de.snenjih.velocloud.agent.runtime.local.tracking.ServiceLogTrack
import de.snenjih.velocloud.agent.services.AbstractService
import oshi.software.os.OSProcess


class LocalService(abstractGroup: AbstractGroup) :
    AbstractService(abstractGroup) {

    private val logTracker = ServiceLogTrack(this)
    private val onlineTrack = OnlineTrack(this)

    var process: Process? = null

    var lastCpuSnapshot: OSProcess? = null
    var lastCpuUpdateTimeStamp = System.currentTimeMillis()

    fun startTracking() {
        this.onlineTrack.start()
        this.logTracker.start()
    }

    fun stopTracking() {
        this.logTracker.close()
        this.onlineTrack.close()
    }

    fun logs(): List<String> = logTracker.cachedLogs

    fun pid() = process?.pid()

}