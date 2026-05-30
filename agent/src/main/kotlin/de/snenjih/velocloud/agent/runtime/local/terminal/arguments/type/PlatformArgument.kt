package de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type

import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.TerminalArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext
import de.snenjih.velocloud.platforms.Platform
import de.snenjih.velocloud.platforms.PlatformPool

class PlatformArgument(key: String = "platform") : TerminalArgument<Platform>(key) {

    override fun buildResult(input: String, context: InputContext): Platform {
        return PlatformPool.find(input)!!
    }

    override fun wrongReason(rawInput: String): String {
        return i18n.get("agent.terminal.setup.argument.platform.wrong")
    }

    override fun defaultArgs(context: InputContext): MutableList<String> {
        return PlatformPool.platforms().stream().map { it.name }.toList()
    }

    override fun predication(rawInput: String): Boolean {
        return PlatformPool.find(rawInput) != null
    }
}