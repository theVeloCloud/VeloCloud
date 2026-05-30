package de.snenjih.velocloud.agent.services

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.shared.template.Template
import de.snenjih.velocloud.v1.services.ServiceBootRequest
import de.snenjih.velocloud.v1.services.ServiceBootResponse
import de.snenjih.velocloud.v1.services.ServiceBootWithConfigurationRequest
import de.snenjih.velocloud.v1.services.ServiceBootWithConfigurationResponse
import de.snenjih.velocloud.v1.services.ServiceControllerGrpc
import de.snenjih.velocloud.v1.services.ServiceFindRequest
import de.snenjih.velocloud.v1.services.ServiceFindResponse
import de.snenjih.velocloud.v1.services.ServiceShutdownRequest
import de.snenjih.velocloud.v1.services.ServiceShutdownResponse
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.grpc.stub.StreamObserver

class ServiceGrpcService : ServiceControllerGrpc.ServiceControllerImplBase() {

    override fun find(request: ServiceFindRequest, responseObserver: StreamObserver<ServiceFindResponse>) {
        val serviceStorage = Agent.runtime.serviceStorage()
        val builder = ServiceFindResponse.newBuilder()

        if (request.hasName()) {
            val service = serviceStorage.find(request.name)
            if(service != null) {
                builder.addServices(service.toSnapshot())
            }
        } else if(request.hasGroupName()) {
            serviceStorage.findByGroup(request.groupName).forEach {
                builder.addServices(it.toSnapshot())
            }
        } else {
            serviceStorage.findAll().forEach {
                if (!request.hasType() || it.type == request.type) {
                    builder.addServices(it.toSnapshot())
                }
            }
        }
        responseObserver.onNext(builder.build())
        responseObserver.onCompleted()
    }

    override fun boot(request: ServiceBootRequest, responseObserver: StreamObserver<ServiceBootResponse>) {
        val groupStorage = Agent.runtime.groupStorage()
        val group = groupStorage.find(request.groupName)
        val builder = ServiceBootResponse.newBuilder()

        if(group == null) {
            responseObserver.onError(StatusRuntimeException(Status.NOT_FOUND))
            return
        }

        // todo duplicated code
        val service = Agent.runtime.factory().generateInstance(group)

        Agent.runtime.serviceStorage().deployAbstractService(service)
        Agent.runtime.factory().bootApplication(service)
        builder.service = service.toSnapshot()

        responseObserver.onNext(builder.build())
        responseObserver.onCompleted()

    }

    override fun bootWithConfiguration(request: ServiceBootWithConfigurationRequest, responseObserver: StreamObserver<ServiceBootWithConfigurationResponse>) {
        val groupStorage = Agent.runtime.groupStorage()
        val group = groupStorage.find(request.groupName)
        val builder = ServiceBootWithConfigurationResponse.newBuilder()

        if(group == null) {
            responseObserver.onError(StatusRuntimeException(Status.NOT_FOUND))
            return
        }

        val service = Agent.runtime.factory().generateInstance(group)

        if(request.hasMinimumMemory()) {
            service.updateMinMemory(request.minimumMemory)
        }
        if(request.hasMaximumMemory()) {
            service.updateMaxMemory(request.maximumMemory)
        }

        val updatedTemplates = service.templates.toMutableList()
        updatedTemplates += Template.fromSnapshotList(request.templatesList)

        request.excludedTemplatesList.forEach { template ->
            updatedTemplates.removeIf { it.name == template.name }
        }

        service.templates = updatedTemplates

        service.properties += request.propertiesMap

        Agent.runtime.serviceStorage().deployAbstractService(service)
        Agent.runtime.factory().bootApplication(service)
        builder.service = service.toSnapshot()

        responseObserver.onNext(builder.build())
        responseObserver.onCompleted()

    }

    override fun shutdown(request: ServiceShutdownRequest, responseObserver: StreamObserver<ServiceShutdownResponse>) {
        val serviceStorage = Agent.runtime.serviceStorage()
        val service = serviceStorage.find(request.name)
        val builder = ServiceShutdownResponse.newBuilder()

        if(service == null) {
            responseObserver.onError(StatusRuntimeException(Status.NOT_FOUND))
            return
        }

        builder.service = service.toSnapshot()
        Agent.runtime.factory().shutdownApplication(service)

        responseObserver.onNext(builder.build())
        responseObserver.onCompleted()
    }

}