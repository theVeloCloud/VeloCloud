package de.snenjih.velocloud.agent.templates

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.v1.templates.TemplateControllerGrpc
import de.snenjih.velocloud.v1.templates.TemplateFindRequest
import de.snenjih.velocloud.v1.templates.TemplateFindResponse
import io.grpc.stub.StreamObserver

class TemplateGrpcService : TemplateControllerGrpc.TemplateControllerImplBase() {

    override fun find(request: TemplateFindRequest, responseObserver: StreamObserver<TemplateFindResponse>) {

        val builder = TemplateFindResponse.newBuilder()
        val templates = Agent.runtime.templateStorage()

        val templatesToReturn = if (request.name.isNotEmpty()) {
            templates.find(request.name)?.let { listOf(it) } ?: emptyList()
        } else {
            templates.findAll()
        }

        for (template in templatesToReturn) {
            builder.addTemplate(template.to())
        }

        responseObserver.onNext(builder.build())
        responseObserver.onCompleted()

    }

}