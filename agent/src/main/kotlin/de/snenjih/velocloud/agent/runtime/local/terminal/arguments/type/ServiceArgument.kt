package de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.TerminalArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext
import de.snenjih.velocloud.agent.services.AbstractService
import de.snenjih.velocloud.shared.service.Service

class ServiceArgument(key: String = "service") : TerminalArgument<AbstractService>(key) {

    override fun buildResult(input: String, context: InputContext): AbstractService {
        // null check is done in the predication method
        return Agent.runtime.serviceStorage().find(input)!!
    }

    override fun defaultArgs(context: InputContext): MutableList<String> {
        return Agent.runtime.serviceStorage().findAll().stream().map { it.name() }.toList()
    }

    override fun predication(rawInput: String): Boolean {
        return Agent.runtime.serviceStorage().find(rawInput) != null
    }
}