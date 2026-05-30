package de.snenjih.velocloud.agent.runtime.k8s

import de.snenjih.velocloud.agent.runtime.RuntimeServiceStorage
import de.snenjih.velocloud.agent.services.AbstractService
import de.snenjih.velocloud.shared.service.SharedBootConfiguration
import de.snenjih.velocloud.v1.groups.GroupType
import de.snenjih.velocloud.v1.services.ServiceSnapshot
import java.util.concurrent.CompletableFuture

class KubernetesRuntimeServiceStorage : RuntimeServiceStorage<KubernetesService> {


    override fun findAll(): List<KubernetesService> {
        TODO("Not yet implemented")
    }

    override fun findAllAsync(): CompletableFuture<List<KubernetesService>> {
        TODO("Not yet implemented")
    }

    override fun find(name: String): KubernetesService? {
        TODO("Not yet implemented")
    }

    override fun findAsync(name: String): CompletableFuture<KubernetesService?> {
        TODO("Not yet implemented")
    }

    override fun findByType(type: GroupType): List<KubernetesService> {
        TODO("Not yet implemented")
    }

    override fun findByTypeAsync(type: GroupType): CompletableFuture<List<KubernetesService>> {
        TODO("Not yet implemented")
    }

    override fun findByGroup(group: de.snenjih.velocloud.shared.groups.Group): List<KubernetesService> {
        TODO("Not yet implemented")
    }

    override fun findByGroupAsync(group: de.snenjih.velocloud.shared.groups.Group): CompletableFuture<List<KubernetesService>> {
        TODO("Not yet implemented")
    }

    override fun findByGroup(group: String): List<KubernetesService> {
        TODO("Not yet implemented")
    }

    override fun findByGroupAsync(group: String): CompletableFuture<List<KubernetesService>> {
        TODO("Not yet implemented")
    }

    override fun bootInstanceWithConfiguration(
        name: String,
        configuration: SharedBootConfiguration
    ): ServiceSnapshot {
        TODO("Not yet implemented")
    }

    override fun shutdownService(name: String): ServiceSnapshot {
        TODO("Not yet implemented")
    }

    override fun deployService(service: KubernetesService) {
        TODO("Not yet implemented")
    }

    override fun dropService(service: KubernetesService) {
        TODO("Not yet implemented")
    }

    override fun implementedService(abstractService: AbstractService): KubernetesService {
        TODO("Not yet implemented")
    }
}