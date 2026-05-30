package de.snenjih.velocloud.agent.runtime.k8s

import de.snenjih.velocloud.agent.groups.AbstractGroup
import de.snenjih.velocloud.agent.runtime.RuntimeFactory
import de.snenjih.velocloud.v1.services.ServiceSnapshot

class KubernetesFactory : RuntimeFactory<KubernetesService> {
    override fun bootApplication(service: KubernetesService) {
        TODO("Not yet implemented")
    }

    override fun shutdownApplication(service: KubernetesService, shutdownCleanUp: Boolean): ServiceSnapshot {
        TODO("Not yet implemented")
    }

    override fun generateInstance(group: AbstractGroup): KubernetesService {
        TODO("Not yet implemented")
    }
}