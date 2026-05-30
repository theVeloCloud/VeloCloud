package de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type

import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.TerminalArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext

class GroupEditFlagArgument(editValue: String = "key") : TerminalArgument<GroupEditFlagArgument.TYPES>(editValue) {

    override fun buildResult(input: String, context: InputContext): TYPES {
        return TYPES.valueOf(input.uppercase())
    }

    override fun defaultArgs(context: InputContext): MutableList<String> {
        return TYPES.entries.map { it.name }.toMutableList()
    }

    enum class TYPES {
        MIN_ONLINE_SERVICES,
        MAX_ONLINE_SERVICES,
        MIN_MEMORY,
        MAX_MEMORY;
    }
}