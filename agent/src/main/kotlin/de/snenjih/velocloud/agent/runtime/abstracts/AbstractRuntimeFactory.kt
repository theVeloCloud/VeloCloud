package de.snenjih.velocloud.agent.runtime.abstracts

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.runtime.RuntimeFactory
import de.snenjih.velocloud.agent.services.AbstractService
import de.snenjih.velocloud.agent.utils.JavaUtils
import de.snenjih.velocloud.common.image.pngToBase64DataUrl
import de.snenjih.velocloud.common.language.Language
import de.snenjih.velocloud.common.os.currentOS
import de.snenjih.velocloud.common.version.velocloudVersion
import de.snenjih.velocloud.platforms.Platform
import de.snenjih.velocloud.platforms.PlatformParameters
import de.snenjih.velocloud.platforms.PlatformPool
import de.snenjih.velocloud.platforms.ServerPlatformForwarding
import de.snenjih.velocloud.shared.events.definitions.service.ServiceChangeStateEvent
import de.snenjih.velocloud.shared.properties.JAVA_PATH
import de.snenjih.velocloud.v1.groups.GroupType
import de.snenjih.velocloud.v1.services.ServiceSnapshot
import de.snenjih.velocloud.v1.services.ServiceState
import org.yaml.snakeyaml.util.Tuple
import java.nio.file.Files
import java.nio.file.Path
import java.util.ArrayList
import java.util.Collections
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.collections.addAll
import kotlin.io.path.createDirectories
import kotlin.io.path.name

abstract class AbstractRuntimeFactory<T : AbstractService>(val factoryPath: Path) : RuntimeFactory<T> {

    val cacheThreadPool: ExecutorService by lazy { Executors.newFixedThreadPool(Agent.config.maxCachingProcesses) }
    val runningCacheProcesses: MutableList<Tuple<String, String>> by lazy {
        Collections.synchronizedList(
            mutableListOf()
        )
    }
    val waitingServices: MutableList<T> by lazy { Collections.synchronizedList(mutableListOf()) }

    /**
     * Boots the given [service].
     * If the service's platform version is not cached, it will be cached first.
     * If the platform is currently being cached, the service will be added to the waiting list
     * and booted as soon as the caching is done.
     */
    override fun bootApplication(service: T) {
        if (service.state != ServiceState.PREPARING) {
            i18n.error("agent.local-runtime.factory.boot.error", service.name(), service.state)
            return
        }

        val platform = service.group().platform()
        val version = service.group().platform.version

        val environment = this.environment(service)

        val path = service.path
        path.createDirectories()

        //loading cache before starting service
        val cacheIsRunning = runningCacheProcesses.any { platform.name == it._1() && version == it._2() }
        if (!platform.cacheExists(version) || cacheIsRunning) {
            waitingServices.add(service)

            if (!cacheIsRunning) {
                this.handleMissingCache(platform, version, environment)
            }
            return
        }

        i18n.info("agent.local-runtime.factory.boot.up", service.name())
        service.state = ServiceState.STARTING
        Agent.eventService.call(ServiceChangeStateEvent(service))

        // copy all templates to the service path
        Agent.runtime.templateStorage().bindTemplate(service)

        // copy the platform files to the service path and setup service
        platform.prepare(path, service.group().platform.version, environment)

        val serverIcon = this.javaClass.classLoader.getResource("server-icon.png")!!
        val serverIconPath = path.resolve("server-icon.png")
        // copy server-icon if not exists
        if (Files.notExists(serverIconPath)) {
            Files.copy(serverIcon.openStream(), serverIconPath)
        }

        this.runRuntimeBoot(service)
    }

    override fun shutdownApplication(service: T, shutdownCleanUp: Boolean): ServiceSnapshot {
        // If the service is already stopping or stopped, return its snapshot
        if (service.state == ServiceState.STOPPING || service.state == ServiceState.STOPPED) {
            return service.toSnapshot()
        }

        service.state = ServiceState.STOPPING
        val eventService = Agent.eventService

        i18n.info("agent.local-runtime.factory.shutdown", service.name())

        // Remove any event subscriptions for this service
        eventService.dropServiceSubscriptions(service)
        // Notify other services that this service is stopping
        eventService.call(ServiceChangeStateEvent(service))

        this.runRuntimeShutdown(service, shutdownCleanUp)

        // Finalize service state and fire shutdown event
        service.state = ServiceState.STOPPED
        Agent.eventProvider().call(ServiceChangeStateEvent(service))
        Agent.runtime.serviceStorage().dropAbstractService(service)

        i18n.info(
            "agent.local-runtime.factory${if (service.isStatic()) ".static" else ""}.shutdown.successful",
            service.name()
        )
        return service.toSnapshot()
    }

    /**
     * Runs the runtime boot process for the given [service].
     * This method is called after all checks are done and the environment is prepared.
     */
    abstract fun runRuntimeBoot(service: T)

    abstract fun runRuntimeShutdown(service: T, shutdownCleanUp: Boolean)

    /**
     * Prepares the environment parameters for the given [service].
     * This includes parameters like hostname, port, server icon, agent port, service name,
     * proxy token, file suffix, and platform file name.
     *
     * @param service The service for which to prepare the environment.
     * @return A [PlatformParameters] object containing all necessary parameters.
     * @see PlatformParameters
     */
    protected fun environment(service: T) : PlatformParameters {
        val version = service.group().platform.version
        val platform = service.group().platform()
        val versionObject = platform.version(version)

        val environment = PlatformParameters(
            versionObject
        )

        val securityProvider = Agent.securityProvider
        val serverIcon = this.javaClass.classLoader.getResource("server-icon.png")!!

        environment.addParameter("hostname", service.hostname)
        environment.addParameter("port", service.port)
        environment.addParameter("server_icon", pngToBase64DataUrl(serverIcon.openStream()))
        environment.addParameter("agent_port", Agent.config.port)
        environment.addParameter("agent_hostname", Agent.runtime.detectLocalAddress())
        environment.addParameter("service-name", service.name())
        environment.addParameter("file_suffix", platform.language.suffix())
        environment.addParameter("filename", service.group().applicationPlatformFile().name)

        val velocityPlatforms = listOf("velocity", "gate")
        val cachedGroups = Agent.runtime.groupStorage().findAll()

        environment.addParameter("velocityProxyToken", securityProvider.proxySecureToken)
        environment.addParameter("forwarding", securityProvider.globalForwarding)
        environment.addParameter("use_modern_forwarding", securityProvider.isModernForwarding())
        environment.addParameter("use_legacy_forwarding", securityProvider.isLegacyForwarding())

        // platforms usage detection for all setup scripts
        PlatformPool.platforms().forEach {
            environment.addParameter(it.name + "_use", cachedGroups.stream().anyMatch { s -> it.name.contains(s.platform.name) })
        }

        // overwrite for special platforms
        environment.addParameter("velocity_use", cachedGroups.stream().anyMatch { velocityPlatforms.contains(it.platform().name) })

        // for proxy detection in platforms
        // if users want to have a custom proxy platform name, they can use the generic parameter above
        // also the transfer proxy platforms will be detected here
        environment.addParameter("proxy_use", cachedGroups.stream().anyMatch { it.platform().type == GroupType.PROXY })

        // general parameters
        environment.addParameter("version", velocloudVersion())
        return environment
    }

    protected fun handleMissingCache(platform: Platform, version: String, environment: PlatformParameters) {
        val platformName = platform.name

        val processEntry = Tuple(platformName, version)
        runningCacheProcesses.add(processEntry)

        cacheThreadPool.execute {
            i18n.info("agent.local-runtime.factory.boot.platform.prepare", version, platformName)
            platform.cachePrepare(version, environment)
            runningCacheProcesses.remove(processEntry)

            val servicesToBoot =
                waitingServices.filter { it.group().platform.name == platform.name && it.group().platform.version == version }
            servicesToBoot.forEach {
                this.bootApplication(it)
            }
            waitingServices.removeAll(servicesToBoot)
        }
    }

    protected fun languageSpecificBootArguments(service: T): ArrayList<String> {
        val platform = service.group().platform()
        val commands = ArrayList<String>()

        when (platform.language) {
            Language.JAVA -> {
                commands.addAll(javaLanguagePath(service))
                commands.addAll(
                    listOf(
                        "-Dterminal.jline=false",
                        "-Dfile.encoding=UTF-8",
                        "-Xms" + service.minMemory + "M",
                        "-Xmx" + service.maxMemory + "M"
                    )
                )

                if (platform.flags.isNotEmpty()) {
                    commands.addAll(platform.flags)
                }

                commands.addAll(
                    listOf(
                        "-jar",
                        service.group().applicationPlatformFile().name
                    )
                )
                commands.addAll(platform.arguments)
            }

            Language.GO, Language.RUST -> {
                commands.addAll(currentOS.executableCurrentDirectoryCommand(service.group().applicationPlatformFile().name))
            }
        }
        return commands
    }

    protected open fun javaLanguagePath(service: T) : List<String> {
        val javaPath = service.group().properties.get(JAVA_PATH)?.takeIf {
            JavaUtils().isValidJavaPath(it)
        } ?: System.getProperty("java.home")
        return listOf("${javaPath}/bin/java")
    }
}