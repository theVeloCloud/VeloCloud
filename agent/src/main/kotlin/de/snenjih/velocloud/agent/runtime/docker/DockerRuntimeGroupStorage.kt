package de.snenjih.velocloud.agent.runtime.docker

import de.snenjih.velocloud.agent.runtime.abstracts.AbstractGroupStorage
import kotlin.io.path.Path

open class DockerRuntimeGroupStorage() : AbstractGroupStorage(Path(("local/groups"))) {


}
