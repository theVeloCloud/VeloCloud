package de.snenjih.velocloud.agent.runtime.k8s

import de.snenjih.velocloud.agent.runtime.Runtime
import de.snenjih.velocloud.agent.runtime.abstracts.AbstractServiceStatsThread
import io.fabric8.kubernetes.client.KubernetesClient

class KubernetesRuntime(client: KubernetesClient) : Runtime() {

    private val groupStorage = KubernetesRuntimeGroupStorage(client)
    private val configHolder = KubernetesRuntimeConfigHolder(client)
    private val serviceStorage = KubernetesRuntimeServiceStorage()
    private val factory = KubernetesFactory()
    private val expender = KubernetesExpender()
    private val templates = KubernetesRuntimeTemplateStorage()

    override fun serviceStorage() = serviceStorage

    override fun groupStorage() = groupStorage

    override fun factory() = factory

    override fun expender() = expender

    override fun templateStorage() = templates

    override fun configHolder() = configHolder

    override fun sendCommand(command: String) {
        TODO("Not yet implemented")
    }

    override fun detectLocalAddress(): String {
        TODO("Not yet implemented")
    }

    override fun serviceStatsThread(): AbstractServiceStatsThread<*> {
        TODO("Not yet implemented")
    }
}