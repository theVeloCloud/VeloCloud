package de.snenjih.velocloud.agent.player

import com.google.protobuf.Any
import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.shared.service.Service
import de.snenjih.velocloud.v1.player.PlayerActorResponse
import de.snenjih.velocloud.v1.player.PlayerConnectActorRequest
import de.snenjih.velocloud.v1.player.PlayerKickActorRequest
import de.snenjih.velocloud.v1.player.PlayerMessageActorRequest
import de.snenjih.velocloud.v1.player.StreamingAlert
import java.util.UUID
import java.util.concurrent.CompletableFuture

private val cachedPlayers = mutableMapOf<UUID, AbstractVelocloudPlayer>()

class PlayerStorageImpl : PlayerStorage {
    override fun addPlayer(player: AbstractVelocloudPlayer) {
        cachedPlayers[player.uniqueId] = player
    }

    override fun removePlayer(uniqueId: UUID) {
        cachedPlayers.remove(uniqueId)
    }

    override fun findAll(): List<AbstractVelocloudPlayer> {
        return cachedPlayers.values.toList()
    }

    override fun findAllAsync(): CompletableFuture<List<AbstractVelocloudPlayer>> {
        return CompletableFuture.completedFuture(findAll())
    }

    override fun findByName(name: String): AbstractVelocloudPlayer? {
        return cachedPlayers.values.find { it.name.equals(name, ignoreCase = true) }
    }

    override fun findByNameAsync(name: String): CompletableFuture<AbstractVelocloudPlayer?> =
        CompletableFuture.completedFuture(findByName(name))

    override fun findByUniqueId(uniqueId: UUID) = cachedPlayers[uniqueId]

    override fun findByUniqueIdAsync(uniqueId: UUID): CompletableFuture<AbstractVelocloudPlayer?> {
        return CompletableFuture.completedFuture(findByUniqueId(uniqueId))
    }

    override fun findByService(serviceName: String): List<AbstractVelocloudPlayer> {
        return cachedPlayers.values.filter { it.currentServerName.equals(serviceName, ignoreCase = true) }
    }

    override fun findByServiceAsync(service: Service): CompletableFuture<List<AbstractVelocloudPlayer>> {
        return CompletableFuture.completedFuture(findByService(service.name()))
    }

    override fun playerCount(): Int = cachedPlayers.size

    override fun messagePlayer(
        uniqueId: UUID,
        message: String
    ): PlayerActorResponse {
        val player =
            Agent.playerStorage.findByUniqueId(uniqueId) ?: return PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player is not online.").build()

        val proxy = Agent.runtime.serviceStorage().find(player.currentProxyName)

        if (proxy == null || !proxy.actorService.isActive()) {
            return PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player proxy server not found or proxy is invalid session type.").build()
        }

        val message = PlayerMessageActorRequest.newBuilder().setUniqueId(uniqueId.toString()).setMessage(message).build()
        proxy.actorService.stream(StreamingAlert.newBuilder().setClassName(message.javaClass.name).setActor(Any.pack(message)).build())
        return PlayerActorResponse.newBuilder().setSuccess(true).build()
    }

    override fun kickPlayer(
        uniqueId: UUID,
        reason: String
    ): PlayerActorResponse {
        val player =
            Agent.playerStorage.findByUniqueId(uniqueId) ?: return PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player is not online.").build()

        val proxy = Agent.runtime.serviceStorage().find(player.currentProxyName)

        if (proxy == null || !proxy.actorService.isActive()) {
            return PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player proxy server not found or proxy is invalid session type.").build()
        }

        val message = PlayerKickActorRequest.newBuilder().setUniqueId(uniqueId.toString()).setReason(reason).build()
        proxy.actorService.stream(StreamingAlert.newBuilder().setClassName(message.javaClass.name).setActor(Any.pack(message)).build())
        return PlayerActorResponse.newBuilder().setSuccess(true).build()
    }

    override fun connectPlayerToService(
        uniqueId: UUID,
        serviceName: String
    ): PlayerActorResponse {
        val player =
            Agent.playerStorage.findByUniqueId(uniqueId) ?: return PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player is not online.").build()

        val proxy = Agent.runtime.serviceStorage().find(player.currentProxyName)

        if (proxy == null || !proxy.actorService.isActive()) {
            return PlayerActorResponse.newBuilder().setSuccess(false)
                .setErrorMessage("Player proxy server not found or proxy is invalid session type.").build()
        }

        val message = PlayerConnectActorRequest.newBuilder().setUniqueId(uniqueId.toString()).setTargetServiceName(serviceName).build()
        proxy.actorService.stream(StreamingAlert.newBuilder().setClassName(message.javaClass.name).setActor(Any.pack(message)).build())
        return PlayerActorResponse.newBuilder().setSuccess(true).build()
    }
}