package de.snenjih.velocloud.agent.runtime.local.terminal.commands

import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.TerminalArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type.KeywordArgument

class CommandSyntax(
    val execution: CommandExecution,
    val description: String?,
    val arguments: MutableList<TerminalArgument<*>>
) {
    fun usage(): String {
        return java.lang.String.join(
            " ", arguments.stream()
                .map { if (it is KeywordArgument) "&f" + it.key else "&8<&f" + it.key + "&8>" }
                .toList()
        ) + (if (description == null) "" else " &8- &7$description")
    }
}