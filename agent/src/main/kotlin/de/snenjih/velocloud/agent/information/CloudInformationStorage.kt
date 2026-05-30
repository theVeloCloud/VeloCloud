package de.snenjih.velocloud.agent.information

import de.snenjih.velocloud.shared.information.CloudInformation
import de.snenjih.velocloud.shared.information.SharedCloudInformationProvider

interface CloudInformationStorage : SharedCloudInformationProvider<CloudInformation> {

    fun addCloudInformation(cloudInformation: CloudInformation)

    fun removeCloudInformation(cloudInformation: CloudInformation)

    fun saveCurrentCloudInformation()

}