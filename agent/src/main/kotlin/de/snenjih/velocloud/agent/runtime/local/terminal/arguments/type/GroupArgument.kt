package de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.groups.AbstractGroup
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.TerminalArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext

class GroupArgument(key: String = "group") : TerminalArgument<AbstractGroup>(key) {

    override fun buildResult(input: String, context: InputContext): AbstractGroup {
        return Agent.runtime.groupStorage().find(input)!!
    }

    override fun defaultArgs(context: InputContext): MutableList<String> {
        return Agent.runtime.groupStorage().findAll().map { it.name }.toMutableList()
    }

    override fun predication(rawInput: String): Boolean {
        return Agent.runtime.groupStorage().find(rawInput) != null
    }
}