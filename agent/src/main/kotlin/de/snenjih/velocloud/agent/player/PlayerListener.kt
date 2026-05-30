package de.snenjih.velocloud.agent.player

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.shared.events.definitions.PlayerJoinEvent
import de.snenjih.velocloud.shared.events.definitions.PlayerLeaveEvent

class PlayerListener {

    init {
        Agent.eventService.subscribe(PlayerJoinEvent::class.java) { event ->
            val player = event.player
            val abstractPlayer = AbstractVelocloudPlayer(
                name = player.name,
                uniqueId = player.uniqueId,
                currentServerName = player.currentServerName,
                currentProxyName = player.currentProxyName
            )

            Agent.playerStorage.addPlayer(abstractPlayer)
        }

        Agent.eventService.subscribe(PlayerLeaveEvent::class.java) { event ->
            Agent.playerStorage.removePlayer(event.player.uniqueId)
        }
    }
}