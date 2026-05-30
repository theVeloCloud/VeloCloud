package de.snenjih.velocloud.agent.platform

import de.snenjih.velocloud.platforms.PlatformPool
import de.snenjih.velocloud.shared.platform.Platform
import de.snenjih.velocloud.shared.platform.PlatformVersion
import de.snenjih.velocloud.shared.platform.SharedPlatformProvider
import de.snenjih.velocloud.v1.groups.GroupType

class PlatformStorageImpl : SharedPlatformProvider<Platform> {
    override fun findAll(): List<Platform> {
        return PlatformPool.platforms()
            .map {
                Platform(
                    it.name,
                    it.type,
                    it.versions.map { v -> PlatformVersion(v.version) })
            }
    }

    override fun find(name: String): Platform? {
        return PlatformPool.platforms()
            .filter { it.name == name }
            .map {
                Platform(
                    it.name,
                    it.type,
                    it.versions.map { v -> PlatformVersion(v.version) })
            }
            .firstOrNull()
    }

    override fun find(type: GroupType): List<Platform> {
        return PlatformPool.platforms()
            .filter { it.type == type }
            .map {
                Platform(
                    it.name,
                    it.type,
                    it.versions.map { v -> PlatformVersion(v.version) })
            }
    }
}