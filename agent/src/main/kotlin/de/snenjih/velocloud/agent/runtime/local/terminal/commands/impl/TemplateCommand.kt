package de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type.KeywordArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.Command

class TemplateCommand : Command("templates", "Manage all your templates") {

    init {
        syntax(execution = {
            i18n.info("agent.terminal.command.templates.info.header", Agent.runtime.templateStorage().availableTemplates().size)
            Agent.runtime.templateStorage().availableTemplates().forEach { template ->
                i18n.info("agent.terminal.command.template.list", template.name, template.size())
            }
        }, KeywordArgument("list"))

    }
}