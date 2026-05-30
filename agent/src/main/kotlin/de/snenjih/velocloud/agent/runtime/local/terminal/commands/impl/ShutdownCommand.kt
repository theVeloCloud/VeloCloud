package de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl

import de.snenjih.velocloud.agent.exitVelocloud
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.Command

class ShutdownCommand : Command("shutdown", "Shuts down the agent", "stop") {

    init {
        defaultExecution {
            exitVelocloud()
        }
    }

}