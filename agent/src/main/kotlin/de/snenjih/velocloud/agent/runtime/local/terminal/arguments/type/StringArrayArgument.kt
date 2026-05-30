package de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type

import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.TerminalArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext

class StringArrayArgument(key: String) : TerminalArgument<String>(key) {

    override fun buildResult(input: String, context: InputContext): String {
        return input
    }
}