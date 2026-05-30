package de.snenjih.velocloud.agent.runtime.local.terminal.commands.impl

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.runtime.local.terminal.commands.Command
import de.snenjih.velocloud.common.os.cpuUsage
import de.snenjih.velocloud.common.os.maxMemory
import de.snenjih.velocloud.common.os.usedMemory
import java.time.Duration

class InfoCommand : Command("info", "Used to display information about the agent") {


    init {
        // call cpuUsage() to ensure the bean is initialized -> this is necessary because the bean might not be initialized yet when the command is executed
        cpuUsage()

        defaultExecution {
            i18n.info("agent.terminal.command.info")
            i18n.info("agent.terminal.command.info.line.1", formatDuration(System.currentTimeMillis() - System.getProperty("velocloud.lifecycle.boot-time").toLong()))
            i18n.info("agent.terminal.command.info.line.2", Agent.runtime.javaClass.simpleName)
            i18n.info("agent.terminal.command.info.line.3", System.getProperty("java.version"))
            i18n.info("agent.terminal.command.info.line.4", cpuUsage(), "%")
            i18n.info("agent.terminal.command.info.line.5", usedMemory())
            i18n.info("agent.terminal.command.info.line.6", maxMemory())
            i18n.info("agent.terminal.command.info.line.7", Agent.eventService.registeredAmount())
            i18n.info("agent.terminal.command.info.security.forwarding", Agent.securityProvider.globalForwarding.name)
        }
    }

     fun formatDuration(millis: Long): String {
        var duration = Duration.ofMillis(millis)

        val days = duration.toDays()
        duration = duration.minusDays(days)

        val hours = duration.toHours()
        duration = duration.minusHours(hours)

        val minutes = duration.toMinutes()
        duration = duration.minusMinutes(minutes)

        val seconds = duration.seconds
        val sb = StringBuilder()

        if (days > 0) sb.append(days).append("d ")
        if (hours > 0 || days > 0) sb.append(hours).append("h ")
        if (minutes > 0 || hours > 0 || days > 0) sb.append(minutes).append("m ")
        if (seconds > 0 || minutes > 0 || hours > 0 || days > 0) sb.append(seconds).append("s ")

        return sb.toString()
    }
}