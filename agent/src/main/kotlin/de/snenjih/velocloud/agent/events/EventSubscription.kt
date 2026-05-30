package de.snenjih.velocloud.agent.events

import de.snenjih.velocloud.shared.service.Service
import de.snenjih.velocloud.v1.proto.EventProviderOuterClass
import io.grpc.stub.ServerCallStreamObserver

class EventSubscription(val service: Service, val sub : ServerCallStreamObserver<EventProviderOuterClass.EventContext>) {

}