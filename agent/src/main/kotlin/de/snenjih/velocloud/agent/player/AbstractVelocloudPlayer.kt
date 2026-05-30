package de.snenjih.velocloud.agent.player

import de.snenjih.velocloud.shared.player.VelocloudPlayer
import java.util.UUID

class AbstractVelocloudPlayer(
    name: String,
    uniqueId: UUID,
    currentServerName: String,
    currentProxyName: String
) : VelocloudPlayer(
    name,
    uniqueId,
    currentServerName,
    currentProxyName
) {

}