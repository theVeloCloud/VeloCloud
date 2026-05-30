package de.snenjih.velocloud.agent.player

import de.snenjih.velocloud.shared.player.SharedPlayerProvider
import java.util.UUID

interface PlayerStorage : SharedPlayerProvider<AbstractVelocloudPlayer> {

    fun addPlayer(player: AbstractVelocloudPlayer)

    fun removePlayer(uniqueId: UUID)

}