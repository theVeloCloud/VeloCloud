package de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type

import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.TerminalArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext

class TextArgument(key: String) : TerminalArgument<String>(key) {

    override fun buildResult(input: String, context: InputContext): String {
        return input
    }

    override fun predication(rawInput: String): Boolean {
        return rawInput.isNotBlank()
    }

    override fun wrongReason(rawInput: String): String {
        return i18n.get("agent.terminal.setup.argument.text.empty")
    }
}
