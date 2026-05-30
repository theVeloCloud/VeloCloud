package de.snenjih.velocloud.agent.runtime.local

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.runtime.RuntimeServiceStorage
import de.snenjih.velocloud.agent.services.AbstractService
import de.snenjih.velocloud.shared.groups.Group
import de.snenjih.velocloud.shared.service.SharedBootConfiguration
import de.snenjih.velocloud.v1.groups.GroupType
import de.snenjih.velocloud.v1.services.ServiceSnapshot
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

class LocalRuntimeServiceStorage : RuntimeServiceStorage<LocalService> {

    private val services = CopyOnWriteArrayList<LocalService>()

    override fun findAll(): List<LocalService> = this.services


    override fun findAllAsync() = CompletableFuture.completedFuture(findAll())

    override fun find(name: String): LocalService? {
        return this.services.stream()
            .filter { it.name() == name }
            .findFirst()
            .orElse(null)
    }

    override fun findAsync(name: String): CompletableFuture<LocalService?> =
        CompletableFuture.completedFuture<LocalService?>(find(name))


    override fun findByType(type: GroupType): List<LocalService> {
        TODO("Not yet implemented")
    }

    override fun findByTypeAsync(type: GroupType): CompletableFuture<List<LocalService>> {
        TODO("Not yet implemented")
    }

    override fun findByGroup(group: Group): List<LocalService> {
        return this.services.stream()
            .filter { it.group() == group }
            .toList()
    }

    override fun findByGroupAsync(group: Group): CompletableFuture<List<LocalService>> {
        TODO("Not yet implemented")
    }

    override fun findByGroup(group: String): List<LocalService> {
        TODO("Not yet implemented")
    }

    override fun findByGroupAsync(group: String): CompletableFuture<List<LocalService>> {
        TODO("Not yet implemented")
    }

    override fun bootInstanceWithConfiguration(
        name: String,
        configuration: SharedBootConfiguration
    ): ServiceSnapshot {
        TODO()
    }

    override fun shutdownService(name: String): ServiceSnapshot {
        return Agent.runtime.factory().shutdownApplication(find(name) ?: throw IllegalArgumentException("Service not found: $name"))
    }

    override fun deployService(service: LocalService) {
        this.services.add(service)
    }

    override fun dropService(service: LocalService) {
        this.services.remove(service)
    }

    override fun implementedService(abstractService: AbstractService): LocalService {
        return abstractService as? LocalService ?: throw IllegalArgumentException("AbstractService must be of type LocalService")
    }
}