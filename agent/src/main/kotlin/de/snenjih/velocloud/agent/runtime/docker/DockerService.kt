package de.snenjih.velocloud.agent.runtime.docker

import de.snenjih.velocloud.agent.groups.AbstractGroup
import de.snenjih.velocloud.agent.services.AbstractService
import de.snenjih.velocloud.agent.utils.IndexDetector
import de.snenjih.velocloud.agent.utils.PortDetector
import de.snenjih.velocloud.shared.service.ServiceInformation
import de.snenjih.velocloud.shared.template.Template
import de.snenjih.velocloud.v1.groups.GroupType
import de.snenjih.velocloud.v1.services.ServiceState


class DockerService(
    name: String,
    index: Int,
    state: ServiceState,
    platformType: GroupType,
    environment: HashMap<String, String>,
    host: String,
    port: Int,
    templates: List<Template>,
    information: ServiceInformation,
    minMemory: Int,
    maxMemory: Int
) : AbstractService(
    name,
    index,
    state,
    platformType,
    environment,
    host,
    port,
    templates,
    information,
    minMemory,
    maxMemory
) {

    var containerId: String? = null

    constructor(group: AbstractGroup) : this(
        group.name,
        IndexDetector.findIndex(group),
        ServiceState.PREPARING,
        group.platform().type,
        hashMapOf(),
        if (group.isProxy()) "0.0.0.0" else "127.0.0.1",
        PortDetector.nextPort(group),
        group.templates,
        ServiceInformation(System.currentTimeMillis()),
        group.minMemory,
        group.maxMemory
    )

    fun changeToContainerHostname(hostname: String) {
        this.hostname  = hostname
    }
}
