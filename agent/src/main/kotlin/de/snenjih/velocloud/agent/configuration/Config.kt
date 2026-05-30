package de.snenjih.velocloud.agent.configuration

import de.snenjih.velocloud.agent.Agent

interface Config {

    fun save(path: String) {
        Agent.runtime.configHolder().write(path, this)
    }
}