package de.snenjih.velocloud.agent.runtime.local.terminal.setup.impl

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.configuration.AgentConfig
import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type.IntArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type.LocaleArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type.YesNotArgument
import de.snenjih.velocloud.agent.runtime.local.terminal.setup.Setup
import de.snenjih.velocloud.agent.runtime.local.terminal.setup.SetupStep

class OnboardingSetup : Setup<AgentConfig>("Onboarding Setup", canExited = false) {

    private val localeArgument = LocaleArgument("locale")
    private val updateArgument = YesNotArgument("auto-update")
    private val portArgument = IntArgument("port", defaultValue = 8932)
    //private val statusArgument = YesNotArgument("status")

    override fun bindQuestion() {
        attach(SetupStep("agent.local-runtime.setup.onboarding.locale", localeArgument) { locale ->
                i18n.overrideLocale(locale)
            })
        attach(SetupStep("agent.local-runtime.setup.onboarding.auto-update", updateArgument))
        attach(SetupStep("agent.local-runtime.setup.onboarding.port", portArgument))
        //attach(SetupStep("agent.local-runtime.setup.onboarding.status", statusArgument))
    }

    override fun onComplete(result: InputContext): AgentConfig {
        val locale = result.arg(localeArgument)
        val autoUpdate = result.arg(updateArgument)
        val port = result.arg(portArgument)
        //val status = result.arg(statusArgument)

        val config = AgentConfig()
        config.locale = locale
        config.autoUpdate = autoUpdate
        config.port = port
        //config.statusLine = status

        config.save("config")

        // todo call sync with coroutines
        Agent.boot()
        return config
    }

}