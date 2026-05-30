package de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.Command

class ReloadCommand : Command("reload", "Reloads the agent configuration") {

    init {
        defaultExecution {
            i18n.info("agent.terminal.command.reload")

            Agent.runtime.groupStorage().reload()

            i18n.info("agent.terminal.command.reload.successful")
        }
    }
}