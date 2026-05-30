package de.snenjih.velocloud.agent.runtime.k8s

import de.snenjih.velocloud.agent.runtime.RuntimeExpender

class KubernetesExpender : RuntimeExpender<KubernetesService> {

    override fun executeCommand(
        service: KubernetesService,
        command: String
    ) : Boolean {
        TODO("Not yet implemented")
    }

    override fun readLogs(
        service: KubernetesService,
        lines: Int
    ): List<String> {
        TODO("Not yet implemented")
    }
}