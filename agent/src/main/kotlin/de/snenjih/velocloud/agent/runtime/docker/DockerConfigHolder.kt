package de.snenjih.velocloud.agent.runtime.docker

import de.snenjih.velocloud.agent.configuration.Config
import de.snenjih.velocloud.agent.runtime.RuntimeConfigHolder
import de.snenjih.velocloud.common.json.GSON
import de.snenjih.velocloud.common.json.PRETTY_GSON
import java.io.File

class DockerConfigHolder : RuntimeConfigHolder {

    override fun <T : Config> read(fileName: String, defaultValue: T): T {
        val file = File("local/configs/$fileName")
        return if (file.exists()) {
            file.reader().use { reader ->
                PRETTY_GSON.fromJson(reader, defaultValue.javaClass)
            }
        } else {
            write(fileName, defaultValue)
            defaultValue
        }
    }

    override fun <T : Config> write(fileName: String, value: T) {
        val file = File("local/configs/$fileName")
        file.parentFile?.mkdirs()
        file.writer().use { writer ->
            PRETTY_GSON.toJson(value, writer)
        }
    }
}