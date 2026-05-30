package de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl

import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.logger
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.Command
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.CommandService

class HelpCommand(private val commandService: CommandService) : Command("help", "Show all available commands", "?") {

    init {
        defaultExecution {
            i18n.info("agent.terminal.command.help.info")
            commandService.commands.forEach {
                logger.info(" &8- &f${it.name}&8: &7${it.description}")
            }
        }
    }
}