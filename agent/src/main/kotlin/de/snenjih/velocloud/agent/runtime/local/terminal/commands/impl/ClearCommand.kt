package de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl

import de.snenjih.velocloud.agent.runtime.local.terminal.JLine3Terminal
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.Command

class ClearCommand(private val terminal : JLine3Terminal) : Command("clear", "Clears the terminal screen", "cc") {

    init {
        defaultExecution {
            terminal.clearScreen()
        }
    }
}