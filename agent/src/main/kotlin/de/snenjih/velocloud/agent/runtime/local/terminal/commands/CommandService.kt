package de.snenjih.velocloud.agent.runtime.local.terminal.commands

import de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl.HelpCommand
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl.InfoCommand
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl.ReloadCommand
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl.ShutdownCommand
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl.UpdaterCommand
import java.util.*

class CommandService {
    val commands = ArrayList<Command>()
    val parser = CommandParser(this)

    init {
        this.registerCommand(UpdaterCommand())
        this.registerCommand(ShutdownCommand())
        this.registerCommand(ReloadCommand())
        this.registerCommand(InfoCommand())
        this.registerCommand(HelpCommand(this))
    }

    fun commandsByName(name: String): MutableList<Command> {
        return commands.stream().filter {
            it!!.name.equals(name, ignoreCase = true) || Arrays.stream(it.aliases).anyMatch({ s -> s.equals(name, ignoreCase = true) })
        }.toList()
    }

    fun registerCommand(command: Command) {
        this.commands.add(command)
    }

    fun registerCommands(vararg commands: Command) {
        for (command in commands) {
            registerCommand(command)
        }
    }

    fun unregisterCommand(command: Command) {
        this.commands.remove(command)
    }

    fun call(commandId: String, args: Array<String>) {
        parser.parse(commandId, args)
    }
}
