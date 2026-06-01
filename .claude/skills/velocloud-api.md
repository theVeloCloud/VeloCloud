---
name: velocloud-api
description: Reference guide for VeloCloud's APIs — gRPC services, module API, shared interfaces, and how plugins/SDK consumers interact with the cloud.
---

# VeloCloud API Reference Skill

Use this skill when working on anything related to VeloCloud's public or internal APIs:
gRPC services, module/plugin development, SDK consumers, shared interfaces.

---

## gRPC Server (agent-side)

Entry point: `agent/src/main/kotlin/de/snenjih/velocloud/agent/grpc/GrpcServerEndpoint.kt`

Default port: **8932** (configured in `AgentConfig`, field `port`).

Registered services:

| gRPC Service class | Package | Handles |
|---|---|---|
| `EventGrpcService` | `agent.events` | Cloud event subscriptions |
| `GroupGrpcService` | `agent.groups` | Group CRUD |
| `ServiceGrpcService` | `agent.services` | Service lifecycle |
| `PlayerGrpcService` | `agent.player` | Online player queries |
| `CloudInformationGrpcService` | `agent.information` | Cloud stats / uptime |
| `PlatformGrpcService` | `agent.platform` | Platform & version info |
| `TemplateGrpcService` | `agent.templates` | Template management |

Each `*GrpcService` class implements the corresponding proto stub. Proto definitions live in the external artifact `de.snenjih.velocloud:proto` (fetched from Reposilite/Maven).

---

## Shared Provider Interfaces (`de.snenjih.velocloud:shared`)

`Agent` (singleton object in `agent/src/main/kotlin/de/snenjih/velocloud/agent/Agent.kt`) extends `VelocloudShared` and overrides these provider methods — this is the contract SDK consumers call:

| Method | Returns | Backed by |
|---|---|---|
| `eventProvider()` | `SharedEventProvider` | `EventService` |
| `serviceProvider()` | `SharedServiceProvider` | `runtime.serviceStorage()` |
| `groupProvider()` | `SharedGroupProvider` | `runtime.groupStorage()` |
| `playerProvider()` | `SharedPlayerProvider` | `PlayerStorageImpl` |
| `cloudInformationProvider()` | `SharedCloudInformationProvider` | `CloudInformationStorageImpl` |
| `platformProvider()` | `SharedPlatformProvider` | `PlatformStorageImpl` |
| `templateProvider()` | `SharedTemplateProvider` | `runtime.templateStorage()` |

To use these from a module or plugin, access them via `VelocloudShared.instance` (or the injected `VelocloudShared` reference passed to `VelocloudModule.onEnable()`).

---

## Module API

Modules are JAR files placed in `local/modules/`. They are loaded by `ModuleProvider`:
`agent/src/main/kotlin/de/snenjih/velocloud/agent/module/ModuleProvider.kt`

### module.json (required inside the JAR)
```json
{
  "id": "my-module",
  "name": "My Module",
  "main": "com.example.MyModule",
  "apiVersion": "3.0.0",
  "loadOrder": "STARTUP"
}
```

- `loadOrder` values: `STARTUP`, `POST_STARTUP`, `LATE`
- `apiVersion` is validated against the running VeloCloud version via `VersionChecker.isCompatible()`

### Module lifecycle
```kotlin
class MyModule : VelocloudModule {
    override fun onEnable() { /* agent fully booted, providers available */ }
    override fun onDisable() { /* cleanup */ }
}
```

`VelocloudModule` and `ModuleMetadata` come from `de.snenjih.velocloud:shared`.

### Reload
`ModuleProvider.reload()` → unloads all (reverse order) → loads all again.
Triggered via the `reload` terminal command.

---

## Finding API code

```bash
# All gRPC service implementations
find agent/src -name "*GrpcService.kt"

# Shared provider interface usages
grep -r "SharedEventProvider\|SharedGroupProvider\|SharedServiceProvider" agent/src --include="*.kt" -l

# Module loading logic
cat agent/src/main/kotlin/de/snenjih/velocloud/agent/module/ModuleProvider.kt

# Platform API (Minecraft platforms + versions)
find platforms/src -name "*.kt" | sort

# Common utilities used in APIs (JSON, version, network)
find common/src -name "*.kt" | sort
```

---

## Platform API (`platforms` module)

`PlatformPool` — singleton registry of all platforms.
- `platforms/src/main/kotlin/de/snenjih/velocloud/platforms/PlatformPool.kt`
- Platforms loaded from `metadata/platforms/*.json` (copied to resources at build time).
- Tasks (download steps) loaded from `metadata/tasks/`.

`Platform` → has `PlatformVersion` list → each version has `PlatformTask` steps.
Task step actions: `PlatformDownloadAction`, `PlatformFileWriteAction`, `PlatformFileUnzipAction`, `PlatformFileMoveAction`, `PlatformFileDeleteAction`, `PlatformDirectoryDeleteAction`, `PlatformFileReplacementAction`, `PlatformFilePropertyUpdateAction`, `PlatformExecuteCommandAction`.

---

## External dependencies / artifacts

| Artifact | Purpose |
|---|---|
| `de.snenjih.velocloud:proto` | gRPC proto stubs |
| `de.snenjih.velocloud:shared` | `VelocloudShared`, `VelocloudModule`, `ModuleMetadata`, all `Shared*Provider` interfaces |

These are fetched from Reposilite (release) or Maven snapshots. Versions in `gradle/libs.versions.toml`.

---

## Key search patterns

```bash
# Find all Shared*Provider implementations in agent
grep -r "implements\|: Shared" agent/src --include="*.kt" -n

# Inspect a specific gRPC service
cat agent/src/main/kotlin/de/snenjih/velocloud/agent/groups/GroupGrpcService.kt

# Check event subscriptions
cat agent/src/main/kotlin/de/snenjih/velocloud/agent/events/EventService.kt
cat agent/src/main/kotlin/de/snenjih/velocloud/agent/events/EventSubscription.kt
```
