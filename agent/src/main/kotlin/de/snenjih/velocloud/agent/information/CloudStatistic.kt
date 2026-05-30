package de.snenjih.velocloud.agent.information

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.runtime.docker.DockerRuntime
import de.snenjih.velocloud.agent.runtime.k8s.KubernetesRuntime
import de.snenjih.velocloud.common.os.maxMemory
import de.snenjih.velocloud.shared.information.CloudInformation

data class CloudStatistic(
    val cpuUsage: Double,
    val usedMemory: Double,
    val subscribedEvents: Int,
    val timestamp: Long
) {

    companion object {
        fun bindCloudInformation(cloudInformation: CloudInformation): CloudStatistic {
            return CloudStatistic(
                cloudInformation.cpuUsage,
                cloudInformation.usedMemory,
                cloudInformation.subscribedEvents,
                cloudInformation.timestamp
            )
        }
    }

    fun toCloudInformation(): CloudInformation {
        val runtime = Agent.runtime
        val runtimeString = if(runtime is KubernetesRuntime) "Kubernetes" else if(runtime is DockerRuntime) "Docker" else "Local"
        return CloudInformation(
            Agent.runtime.started(),
            runtimeString,
            System.getProperty("java.version"),
            cpuUsage,
            usedMemory,
            maxMemory(),
            subscribedEvents,
            timestamp
        )
    }

}