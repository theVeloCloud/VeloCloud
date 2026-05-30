package de.snenjih.velocloud.agent

import de.snenjih.velocloud.agent.configuration.AgentConfig
import de.snenjih.velocloud.agent.detector.DetectorFactoryThread
import de.snenjih.velocloud.agent.detector.OnlineStateDetector
import de.snenjih.velocloud.agent.events.EventService
import de.snenjih.velocloud.agent.grpc.GrpcServerEndpoint
import de.snenjih.velocloud.agent.i18n.I18nVelocloudAgent
import de.snenjih.velocloud.agent.module.ModuleProvider
import de.snenjih.velocloud.agent.player.PlayerListener
import de.snenjih.velocloud.agent.player.PlayerStorageImpl
import de.snenjih.velocloud.agent.runtime.Runtime
import de.snenjih.velocloud.agent.runtime.local.LocalRuntime
import de.snenjih.velocloud.agent.security.SecurityProvider
import de.snenjih.velocloud.common.version.velocloudVersion
import de.snenjih.velocloud.platforms.PlatformPool
import de.snenjih.velocloud.shared.VelocloudShared
import de.snenjih.velocloud.shared.events.SharedEventProvider
import de.snenjih.velocloud.shared.groups.SharedGroupProvider
import de.snenjih.velocloud.shared.player.SharedPlayerProvider
import de.snenjih.velocloud.shared.service.SharedServiceProvider
import de.snenjih.velocloud.shared.information.SharedCloudInformationProvider
import de.snenjih.velocloud.agent.information.CloudInformationStorageImpl
import de.snenjih.velocloud.agent.platform.PlatformStorageImpl
import de.snenjih.velocloud.shared.platform.SharedPlatformProvider
import de.snenjih.velocloud.shared.template.SharedTemplateProvider
import de.snenjih.velocloud.updater.Updater
import org.apache.logging.log4j.LogManager

// global terminal instance for the agent
// this is used to print messages to the console
var logger = initLogging()
val i18n = I18nVelocloudAgent()

object Agent : VelocloudShared(true) {

    val runtime: Runtime
    val eventService = EventService()
    val securityProvider = SecurityProvider()
    val moduleProvider = ModuleProvider()

    lateinit var config: AgentConfig

    private val grpcServerEndpoint = GrpcServerEndpoint()
    private val onlineStateDetector = DetectorFactoryThread.bindDetector(OnlineStateDetector())

    val playerStorage = PlayerStorageImpl()
    val cloudInformationStorage = CloudInformationStorageImpl()
    val platformStorage = PlatformStorageImpl()

    init {
        // display the default log information
        i18n.info("agent.starting", velocloudVersion())

        if (velocloudVersion().endsWith("-SNAPSHOT")) {
            i18n.warn("agent.version.warn")
        }

        if (!Updater.hasSyncGitHubVersion()) {
            i18n.warn("agent.version.unavailable")
        }

        this.checkForUpdates()
        this.runtime = Runtime.create()
        this.runtime.initialize()
    }

    /**
     * The boot method is called to start the agent.
     * Its seperated from the constructor to allow for an onboarding setup.
     * The context must be fully initialized before calling this method.
     */
    fun boot() {
        // read all information about the runtime config
        // this is done before the runtime is initialized
        this.config = this.runtime.configHolder().read("config", AgentConfig())

        if (config.autoUpdate && Updater.newVersionAvailable()) {

            if (this.runtime is LocalRuntime) {
                this.runtime.terminal.clearScreen()
            }

            exitVelocloud(cleanShutdown = true, shouldUpdate = true)
            return
        }

        this.moduleProvider.loadModules()

        this.grpcServerEndpoint.connect(this.config.port)

        this.runtime.prepareBoot()

        val groups = runtime.groupStorage().findAll()

        i18n.info("agent.starting.runtime", runtime::class.simpleName)
        i18n.info(
            "agent.starting.groups.count",
            groups.size,
            groups.joinToString(separator = "&8, &7") { it.name })
        i18n.info("agent.starting.platforms.count", PlatformPool.size(), PlatformPool.versionSize())
        i18n.info("agent.starting.successful")

        this.onlineStateDetector.detect()
        PlayerListener()
    }

    /**
     * Close the agent and all its resources.
     * This method will shut down the runtime, close the gRPC server endpoint,
     * and close the online state detector.
     */
    fun close() {
        this.runtime.shutdown()
        this.grpcServerEndpoint.close()
        this.onlineStateDetector.close()
    }

    /**
     * This method check for updates of the agent.
     * If a new version is available, it will log the information.
     */
    fun checkForUpdates() {
        if (Updater.newVersionAvailable()) {
            logger.info("A new version of the agent is available: ${Updater.latestVersion()}")
            return
        }
        logger.info("You are running the latest version of the agent.")
    }

    override fun eventProvider() = this.eventService

    override fun serviceProvider() = this.runtime.serviceStorage()

    override fun groupProvider() = this.runtime.groupStorage()

    override fun playerProvider() = this.playerStorage

    override fun cloudInformationProvider() = this.cloudInformationStorage

    override fun platformProvider() = this.platformStorage

    override fun templateProvider() = this.runtime.templateStorage()
}