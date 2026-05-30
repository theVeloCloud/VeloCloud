package de.snenjih.velocloud.agent.runtime.local

import de.snenjih.velocloud.agent.runtime.abstracts.AbstractServiceStatsThread
import de.snenjih.velocloud.common.math.convertBytesToMegabytes
import oshi.SystemInfo
import java.math.BigDecimal
import java.math.RoundingMode

class LocalServiceStatsThread : AbstractServiceStatsThread<LocalService>() {

    private val os = SystemInfo().operatingSystem

    override fun detectService(service: LocalService) {
        if (service.pid() == null) {
            return
        }

        if (service.lastCpuSnapshot == null) {
            service.lastCpuSnapshot = os.getProcess(service.pid()!!.toInt());
            return
        }

        val currentSnapshot = os!!.getProcess(service.pid()!!.toInt())

        if (currentSnapshot != null) {
            service.lastCpuSnapshot = currentSnapshot
            val cpu = currentSnapshot.getProcessCpuLoadBetweenTicks(service.lastCpuSnapshot)

            service.lastCpuUpdateTimeStamp = System.nanoTime()

            service.updateCpuUsage(BigDecimal(cpu).setScale(2, RoundingMode.HALF_UP).toDouble())
            service.updateMemoryUsage(convertBytesToMegabytes(currentSnapshot.residentSetSize))
        }
    }
}