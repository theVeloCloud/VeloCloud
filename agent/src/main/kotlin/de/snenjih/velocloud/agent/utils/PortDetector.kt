package de.snenjih.velocloud.agent.utils

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.groups.AbstractGroup
import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.shared.properties.START_PORT
import java.net.InetSocketAddress
import java.net.ServerSocket

class PortDetector {

    companion object {
        fun nextPort(abstractGroup: AbstractGroup): Int {
            var port = abstractGroup.platform().defaultStartPort ?: 30000

            val startPortProperty = abstractGroup.properties.get(START_PORT)
            if (startPortProperty != null) {
                try {
                    port = startPortProperty
                } catch (_: NumberFormatException) {
                    i18n.error("agent.utils.port-detector.port.cast.error", startPortProperty, abstractGroup.name)
                }
            }

            while (isPortUsed(port)) {
                port += 1
            }
            return port
        }

        private fun isPortUsed(port: Int): Boolean {
            for (service in Agent.runtime.serviceStorage().findAll()) {
                if (service.port == port) {
                    return true
                }
            }
            try {
                ServerSocket().use { serverSocket ->
                    serverSocket.bind(InetSocketAddress(port))
                    return false
                }
            } catch (_: Exception) {
                return true
            }
        }
    }
}