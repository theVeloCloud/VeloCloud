package de.snenjih.velocloud.agent.runtime

import de.snenjih.velocloud.agent.services.AbstractService
import de.snenjih.velocloud.shared.service.SharedServiceProvider

interface RuntimeServiceStorage<S : AbstractService> : SharedServiceProvider<S> {

    fun deployService(service: S)

    fun deployAbstractService(abstractService: AbstractService) {
        deployService(implementedService(abstractService))
    }

    fun dropService(service: S)

    fun dropAbstractService(abstractService: AbstractService) {
        dropService(implementedService(abstractService))
    }

    fun implementedService(abstractService: AbstractService) : S

}