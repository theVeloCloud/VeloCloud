package de.snenjih.velocloud.agent.database

import com.google.protobuf.Empty
import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.v1.database.DatabaseConfigControllerGrpc
import de.snenjih.velocloud.v1.database.DatabaseConfigResponse
import io.grpc.stub.StreamObserver

class DatabaseConfigGrpcService : DatabaseConfigControllerGrpc.DatabaseConfigControllerImplBase() {

    override fun fetchCloudDatabaseConfig(
        request: Empty,
        responseObserver: StreamObserver<DatabaseConfigResponse>
    ) {
        val db = Agent.config.database
        val response = if (db == null) {
            DatabaseConfigResponse.newBuilder().setConfigured(false).build()
        } else {
            DatabaseConfigResponse.newBuilder()
                .setConfigured(true)
                .setHost(db.host)
                .setPort(db.port)
                .setDatabase(db.database)
                .setUsername(db.username)
                .setPassword(db.password)
                .setPoolSize(db.poolSize)
                .build()
        }
        responseObserver.onNext(response)
        responseObserver.onCompleted()
    }
}
