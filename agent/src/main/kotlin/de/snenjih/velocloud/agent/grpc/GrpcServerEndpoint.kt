package de.snenjih.velocloud.agent.grpc

import de.snenjih.velocloud.agent.database.DatabaseConfigGrpcService
import de.snenjih.velocloud.agent.events.EventGrpcService
import de.snenjih.velocloud.agent.groups.GroupGrpcService
import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.player.PlayerGrpcService
import de.snenjih.velocloud.agent.services.ServiceGrpcService
import de.snenjih.velocloud.agent.information.CloudInformationGrpcService
import de.snenjih.velocloud.agent.platform.PlatformGrpcService
import de.snenjih.velocloud.agent.templates.TemplateGrpcService
import io.grpc.Server
import io.grpc.ServerBuilder

class GrpcServerEndpoint {

    private lateinit var server: Server

    fun connect(port: Int) {
        this.server = ServerBuilder.forPort(port)
            .addService(EventGrpcService())
            .addService(GroupGrpcService())
            .addService(ServiceGrpcService())
            .addService(PlayerGrpcService())
            .addService(CloudInformationGrpcService())
            .addService(PlatformGrpcService())
            .addService(TemplateGrpcService())
            .addService(DatabaseConfigGrpcService())
            .build()
        this.server.start()
        i18n.info("agent.starting.grpc.successful", port)
    }

    fun close() {
        if (::server.isInitialized) {
            server.shutdown()
        }
    }
}