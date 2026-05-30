package de.snenjih.velocloud.agent.runtime

import de.snenjih.velocloud.agent.services.AbstractService
import de.snenjih.velocloud.agent.utils.Reloadable
import de.snenjih.velocloud.shared.template.SharedTemplateProvider
import de.snenjih.velocloud.shared.template.Template

interface RuntimeTemplateStorage<T : Template, out S : AbstractService> : SharedTemplateProvider<T>, Reloadable {

    fun availableTemplates() : List<Template>

    fun bindTemplate(service: @UnsafeVariance S)

    fun saveTemplate(template: Template, service: @UnsafeVariance S)

    fun templates(service: @UnsafeVariance S): List<Template>

    fun create(name: String): T

    fun delete(template: T)

    fun update(template: T, newName: String)

}