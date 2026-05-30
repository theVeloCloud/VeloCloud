package de.snenjih.velocloud.agent.utils

import de.snenjih.velocloud.agent.Agent
import de.snenjih.velocloud.agent.groups.AbstractGroup

class IndexDetector {

    companion object {
        fun findIndex(group: AbstractGroup): Int {
            var id = 1
            while (Agent.runtime.serviceStorage().findAll().stream()
                    .anyMatch { it.groupName == group.name && it.id == id }
            ) {
                id++
            }
            return id
        }
    }

}