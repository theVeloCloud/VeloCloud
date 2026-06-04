package de.snenjih.velocloud.agent.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import de.snenjih.velocloud.agent.configuration.DatabaseConfig
import de.snenjih.velocloud.agent.i18n

class DatabaseManager(private val config: DatabaseConfig) {

    val dataSource: HikariDataSource

    init {
        try {
            val hc = HikariConfig().apply {
                jdbcUrl = "jdbc:mysql://${config.host}:${config.port}/${config.database}" +
                          "?useSSL=false&autoReconnect=true&characterEncoding=utf8"
                username = config.username
                password = config.password
                maximumPoolSize = config.poolSize
                minimumIdle = 1
                connectionTimeout = 10_000
                idleTimeout = 60_000
                maxLifetime = 1_800_000
                poolName = "VeloCloud-CloudDB"
            }
            dataSource = HikariDataSource(hc)
            i18n.info("agent.database.connected", config.host, config.port, config.database)
        } catch (e: Exception) {
            i18n.error("agent.database.connection.failed", e.message ?: "unknown")
            throw e
        }
    }

    fun close() {
        if (!dataSource.isClosed) {
            dataSource.close()
            i18n.info("agent.database.closed")
        }
    }
}
