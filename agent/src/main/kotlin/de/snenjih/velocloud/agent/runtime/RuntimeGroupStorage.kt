package de.snenjih.velocloud.agent.runtime

import de.snenjih.velocloud.agent.groups.AbstractGroup
import de.snenjih.velocloud.agent.utils.Reloadable
import de.snenjih.velocloud.shared.groups.SharedGroupProvider

interface RuntimeGroupStorage : SharedGroupProvider<AbstractGroup>, Reloadable {

    override fun reload() {
        // Default implementation does nothing
    }
}