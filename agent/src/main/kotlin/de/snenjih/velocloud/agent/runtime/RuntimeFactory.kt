package de.snenjih.velocloud.agent.runtime

import de.snenjih.velocloud.agent.groups.AbstractGroup
import de.snenjih.velocloud.agent.services.AbstractService
import de.snenjih.velocloud.v1.services.ServiceSnapshot

interface RuntimeFactory<out T : AbstractService> {

    fun bootApplication(service: @UnsafeVariance T)

    fun shutdownApplication(service: @UnsafeVariance T, shutdownCleanUp : Boolean = true): ServiceSnapshot

    fun generateInstance(group: AbstractGroup) : T

}