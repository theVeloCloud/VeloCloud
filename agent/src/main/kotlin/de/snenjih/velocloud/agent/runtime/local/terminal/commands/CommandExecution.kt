package de.snenjih.velocloud.agent.runtime.local.terminal.commands

import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext

fun interface CommandExecution {
    fun execute(inputContext: InputContext)
}