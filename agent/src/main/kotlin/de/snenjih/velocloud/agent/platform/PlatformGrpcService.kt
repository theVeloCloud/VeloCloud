package de.snenjih.velocloud.agent.platform

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.v1.platform.PlatformControllerGrpc
import de.snenjih.velocloud.v1.platform.PlatformFindRequest
import de.snenjih.velocloud.v1.platform.PlatformFindResponse
import io.grpc.stub.StreamObserver

class PlatformGrpcService : PlatformControllerGrpc.PlatformControllerImplBase() {

    override fun find(request: PlatformFindRequest, responseObserver: StreamObserver<PlatformFindResponse>) {
        val builder = PlatformFindResponse.newBuilder()
        val platformStorage = Agent.platformStorage

        if(request.hasName()) {
            builder.addPlatforms(platformStorage.find(request.name)?.toSnapshot())
        } else if(request.hasType()) {
            builder.addAllPlatforms(platformStorage.find(request.type).map { it.toSnapshot() })
        } else {
            builder.addAllPlatforms(platformStorage.findAll().map { it.toSnapshot() })
        }

        responseObserver.onNext(builder.build())
        responseObserver.onCompleted()
    }

}