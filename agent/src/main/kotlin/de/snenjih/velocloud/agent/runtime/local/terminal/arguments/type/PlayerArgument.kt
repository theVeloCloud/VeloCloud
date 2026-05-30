package de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.TerminalArgument
import de.snenjih.velocloud.shared.player.VelocloudPlayer

class PlayerArgument : TerminalArgument<VelocloudPlayer>("player") {

    override fun buildResult(
        input: String,
        context: InputContext
    ): VelocloudPlayer {
        return Agent.playerStorage.findByName(input)
            ?: throw IllegalArgumentException("Player with name $input not found")
    }

    override fun defaultArgs(context: InputContext): MutableList<String> {
        val args = mutableListOf<String>()

        Agent.playerStorage.findAll().forEach {
            args.add(it.name)
        }

        return args
    }

    override fun predication(rawInput: String): Boolean {
        return Agent.playerStorage.findAll().any {
            it.name.equals(rawInput, ignoreCase = true)
        }
    }
}