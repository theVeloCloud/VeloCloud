package de.snenjih.velocloud.agent.configuration

import java.util.Locale

data class DatabaseConfig(
    var host: String = "localhost",
    var port: Int = 3306,
    var database: String = "velocloud",
    var username: String = "root",
    var password: String = "",
    var poolSize: Int = 10
)

data class AgentConfig(
    var locale: Locale = Locale.ENGLISH,
    var autoUpdate: Boolean = true,
    var port: Int = 8932,
    var runtime: String = "auto",
    //var statusLine: Boolean = true,
    var maxConcurrentServersStarts: Int = 4,
    var maxCachingProcesses: Int = 4,
    var maxCPUPercentageToStart: Double = 75.0,
    var database: DatabaseConfig? = null
) : Config