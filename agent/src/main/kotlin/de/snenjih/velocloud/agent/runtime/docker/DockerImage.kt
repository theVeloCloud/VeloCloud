package de.snenjih.velocloud.agent.runtime.docker

import com.github.dockerjava.api.DockerClient
import de.snenjih.velocloud.shared.template.Template

class DockerImage(val client: DockerClient, name: String) : Template(name) {

    override fun size(): String {
        val info = client.inspectImageCmd(name).exec()
        val sizeBytes = info.size ?: 0L

        return humanReadableSize(sizeBytes)
    }
}