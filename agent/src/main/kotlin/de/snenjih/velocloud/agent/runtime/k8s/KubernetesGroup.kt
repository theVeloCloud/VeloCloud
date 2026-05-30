package de.snenjih.velocloud.agent.runtime.k8s

import de.snenjih.velocloud.shared.service.Service
import io.fabric8.kubernetes.api.model.Namespaced
import io.fabric8.kubernetes.client.CustomResource
import io.fabric8.kubernetes.model.annotation.Group
import io.fabric8.kubernetes.model.annotation.Kind
import io.fabric8.kubernetes.model.annotation.Version

@Kind("Group")
@Version("v1")
@Group("velocloud.de")
class KubernetesGroup(val name: String) : CustomResource<Service, KubernetesGroupStatus>(), Namespaced {

}