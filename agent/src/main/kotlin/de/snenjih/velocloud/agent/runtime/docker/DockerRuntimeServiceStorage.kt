package de.snenjih.velocloud.agent.runtime.docker

import com.github.dockerjava.api.DockerClient
import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.runtime.RuntimeServiceStorage

import de.snenjih.velocloud.agent.services.AbstractService
import de.snenjih.velocloud.shared.groups.Group
import de.snenjih.velocloud.shared.service.SharedBootConfiguration
import de.snenjih.velocloud.v1.groups.GroupType
import de.snenjih.velocloud.v1.services.ServiceSnapshot
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CopyOnWriteArrayList

class DockerRuntimeServiceStorage(val client: DockerClient) : RuntimeServiceStorage<DockerService> {
    private val services = CopyOnWriteArrayList<DockerService>()

    override fun findAll(): List<DockerService> = this.services


    override fun findAllAsync() = CompletableFuture.completedFuture(findAll())

    override fun find(name: String): DockerService? {
        return this.services.stream()
            .filter { it.name() == name }
            .findFirst()
            .orElse(null)
    }

    override fun findAsync(name: String): CompletableFuture<DockerService?> =
        CompletableFuture.completedFuture<DockerService?>(find(name))


    override fun findByType(type: GroupType): List<DockerService> {
        TODO("Not yet implemented")
    }

    override fun findByTypeAsync(type: GroupType): CompletableFuture<List<DockerService>> {
        TODO("Not yet implemented")
    }

    override fun findByGroup(group: Group): List<DockerService> {
        return this.services.stream()
            .filter { it.group() == group }
            .toList()
    }

    override fun findByGroupAsync(group: Group): CompletableFuture<List<DockerService>> {
        TODO("Not yet implemented")
    }

    override fun findByGroup(group: String): List<DockerService> {
        TODO("Not yet implemented")
    }

    override fun findByGroupAsync(group: String): CompletableFuture<List<DockerService>> {
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

    override fun deployService(service: DockerService) {
        this.services.add(service)
    }

    override fun dropService(service: DockerService) {
        this.services.remove(service)
    }

    override fun implementedService(abstractService: AbstractService): DockerService {
        return abstractService as? DockerService ?: throw IllegalArgumentException("AbstractService must be of type LocalService")
    }
}