package de.snenjih.velocloud.agent.runtime

import de.snenjih.velocloud.agent.configuration.Config

interface RuntimeConfigHolder {

    fun <T : Config> read(fileName: String, defaultValue: T) : T

    fun <T : Config> write(fileName: String, value: T)

}