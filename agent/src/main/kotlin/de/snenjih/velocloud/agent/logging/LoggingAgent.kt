package de.snenjih.velocloud.agent.logging

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.runtime.local.LocalRuntime
import de.snenjih.velocloud.agent.shutdownProcess
import org.apache.logging.log4j.core.Appender
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.Layout
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.appender.AbstractAppender
import org.apache.logging.log4j.core.config.plugins.*
import org.apache.logging.log4j.core.layout.PatternLayout

@Plugin(name = "LoggingAgent", category = "Core", elementType = "appender")
class LoggingAgent(
    name: String,
    filter: Filter?,
    layout: Layout<*>,
) : AbstractAppender(name, filter, layout, true, null) {

    override fun append(event: LogEvent) {
        val formatted = layout.toSerializable(event).toString()

        // local runtime terminal
        if (Agent.runtime is LocalRuntime && !shutdownProcess()) {
            Agent.runtime.terminal.displayApproved(formatted)
        } else {
            print(formatted)
        }
    }

    companion object {
        @PluginFactory
        @JvmStatic
        fun create(
            @PluginAttribute("name") name: String,
            @PluginElement("Layout") layout: Layout<*>?,
        ): Appender {
            val layoutUsed = layout ?: PatternLayout.createDefaultLayout()
            return LoggingAgent(name, null, layoutUsed)
        }
    }
}