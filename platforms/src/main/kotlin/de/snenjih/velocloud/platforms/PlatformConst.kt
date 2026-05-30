package de.snenjih.velocloud.platforms

import com.google.gson.GsonBuilder
import de.snenjih.velocloud.common.json.RuntimeTypeAdapterFactory
import de.snenjih.velocloud.platforms.bridge.Bridge
import de.snenjih.velocloud.platforms.tasks.actions.PlatformAction
import de.snenjih.velocloud.platforms.tasks.actions.PlatformDirectoryDeleteAction
import de.snenjih.velocloud.platforms.tasks.actions.PlatformDownloadAction
import de.snenjih.velocloud.platforms.tasks.actions.PlatformExecuteCommandAction
import de.snenjih.velocloud.platforms.tasks.actions.PlatformFileDeleteAction
import de.snenjih.velocloud.platforms.tasks.actions.PlatformFileMoveAction
import de.snenjih.velocloud.platforms.tasks.actions.PlatformFilePropertyUpdateAction
import de.snenjih.velocloud.platforms.tasks.actions.PlatformFileReplacementAction
import de.snenjih.velocloud.platforms.tasks.actions.PlatformFileUnzipAction
import de.snenjih.velocloud.platforms.tasks.actions.PlatformFileWriteAction
import kotlin.io.path.Path

val PLATFORM_PATH = Path("local/metadata")
const val PLATFORM_METADATA_URL = "https://raw.githubusercontent.com/theVeloCloud/velocloud-metadata/refs/heads/master/"

val PLATFORM_GSON =
    GsonBuilder().setPrettyPrinting().serializeNulls()
        .registerTypeHierarchyAdapter(PlatformVersion::class.java, PlatformVersionSerializer())
        .registerTypeAdapter(Platform::class.java, PlatformDeserializer())
        .registerTypeAdapterFactory(
            RuntimeTypeAdapterFactory
                .of(PlatformAction::class.java, "type") // "type" ist das Typ-Merkmal im JSON
                .registerSubtype(PlatformFileReplacementAction::class.java, "PlatformFileReplacementAction")
                .registerSubtype(PlatformFileWriteAction::class.java, "PlatformFileWriteAction")
                .registerSubtype(PlatformFilePropertyUpdateAction::class.java, "PlatformFilePropertyUpdateAction")
                .registerSubtype(PlatformFileUnzipAction::class.java, "PlatformFileUnzipAction")
                .registerSubtype(PlatformFileDeleteAction::class.java, "PlatformFileDeleteAction")
                .registerSubtype(PlatformExecuteCommandAction::class.java, "PlatformExecuteCommandAction")
                .registerSubtype(PlatformDirectoryDeleteAction::class.java, "PlatformDirectoryDeleteAction")
                .registerSubtype(PlatformFileMoveAction::class.java, "PlatformFileMoveAction")
                .registerSubtype(PlatformDownloadAction::class.java, "PlatformDownloadAction")
        )
        .create()
