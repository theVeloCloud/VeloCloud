package de.snenjih.velocloud.agent.runtime.local.terminal.setup.impl

import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext
import de.snenjih.velocloud.agent.runtime.local.terminal.setup.Setup
import de.snenjih.velocloud.platforms.Platform

class CustomPlatformSetup : Setup<Platform>("Custom platform setup") {

    override fun bindQuestion() {
        TODO("Not yet implemented")
    }

    override fun onComplete(result: InputContext): Platform {
        TODO("Not yet implemented")
    }
}