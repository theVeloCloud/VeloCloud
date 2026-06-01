---
name: velocloud-agent
description: Navigation guide for the VeloCloud Agent codebase — entry points, console commands (adding/editing), terminal architecture, runtime system, and key subsystems.
---

# VeloCloud Agent Skill

Use this skill when working in the `agent/` module: adding or editing console commands,
understanding the terminal/command system, navigating subsystems, or tracing the boot flow.

---

## Entry Points

| File | Role |
|---|---|
| `agent/src/main/kotlin/de/snenjih/velocloud/agent/AgentBoot.kt` | `main()` — initialises logging, creates `Agent` singleton |
| `agent/src/main/kotlin/de/snenjih/velocloud/agent/Agent.kt` | `object Agent` — the entire cloud, wires all subsystems |
| `agent/src/main/kotlin/de/snenjih/velocloud/agent/runtime/local/LocalRuntime.kt` | Boot for the Local runtime (also where terminal commands are registered) |

### Boot sequence
1. `AgentBoot.main()` → creates `Agent` object
2. `Agent.init` → picks runtime via `Runtime.create()` → calls `runtime.initialize()`
3. `LocalRuntime.initialize()` → starts `JLine3Terminal` → starts `OnboardingSetup` if no `config.json`, else calls `Agent.boot()`
4. `Agent.boot()` → reads `AgentConfig` → loads modules → starts gRPC → `runtime.prepareBoot()` → starts service stats thread → `runtime.boot()`
5. `LocalRuntime.boot()` → registers all terminal commands → starts runtime queue + cloud info thread

---

## Console Command System

### Architecture

```
CommandService          — registry, holds ArrayList<Command>
  └─ CommandParser      — tokenises input, matches syntaxes
       └─ Command (abstract)
            ├─ commandSyntaxes: List<CommandSyntax>
            └─ defaultExecution: CommandExecution?

CommandSyntax           — one subcommand pattern: list of TerminalArguments + execution lambda
TerminalArgument<T>     — typed argument (keyword, int, group name, etc.)
InputContext            — passed to the execution lambda; call context.arg(argument) to get typed values
```

Key files:
- `agent/.../terminal/commands/Command.kt` — abstract base
- `agent/.../terminal/commands/CommandService.kt` — registry + `registerCommand()` / `call()`
- `agent/.../terminal/commands/CommandSyntax.kt`
- `agent/.../terminal/commands/CommandParser.kt`
- `agent/.../terminal/commands/CommandExecution.kt`
- `agent/.../terminal/arguments/TerminalArgument.kt`

### Existing commands (all in `agent/.../terminal/commands/impl/`)

| Class | Command name | Aliases | Summary |
|---|---|---|---|
| `ClearCommand` | `clear` | — | Clears terminal screen |
| `GroupCommand` | `group` | — | list, info, create, edit, delete, shutdownAll, start |
| `HelpCommand` | `help` | — | Lists all commands and syntaxes |
| `InfoCommand` | `info` | — | Cloud info / uptime |
| `PlatformCommand` | `platform` | — | Platform list / version info |
| `PlayersCommand` | `players` | — | List online players |
| `ReloadCommand` | `reload` | — | Reloads modules |
| `ServiceCommand` | `service` | `ser` | list, info, shutdown, logs, copy, execute, screen |
| `ShutdownCommand` | `shutdown` | — | Graceful agent shutdown |
| `TemplateCommand` | `templates` | — | list templates |
| `UpdaterCommand` | `updater` | — | Update check |

### How to add a new command

1. **Create** a new class in `agent/.../terminal/commands/impl/MyCommand.kt`:

```kotlin
class MyCommand : Command("mycommand", "Short description", "alias1") {
    init {
        // Default execution (no sub-arguments)
        defaultExecution {
            i18n.info("agent.terminal.command.mycommand.usage")
        }

        // Sub-command: mycommand list
        syntax(execution = {
            // do something
        }, KeywordArgument("list"))

        // Sub-command with a typed argument: mycommand info <groupName>
        val groupArg = GroupArgument()
        syntax(execution = { context ->
            val group = context.arg(groupArg)
            i18n.info("agent.terminal.command.mycommand.info", group.name)
        }, KeywordArgument("info"), groupArg)
    }
}
```

2. **Register** it in `LocalRuntime.boot()`:

```kotlin
// agent/.../runtime/local/LocalRuntime.kt  →  boot()
terminal.commandService.registerCommand(MyCommand())
```

If the command needs `RuntimeGroupStorage` or `JLine3Terminal`, inject them the same way `GroupCommand` does (passed as constructor params from `LocalRuntime.boot()`).

3. **Add i18n keys** in `agent/src/main/resources/i18n/velocloud-agent_en.properties` (and other locales).

### Available argument types (`agent/.../terminal/arguments/type/`)

| Class | Key example | Description |
|---|---|---|
| `KeywordArgument` | `"list"` | Fixed literal keyword |
| `GroupArgument` | `"group"` | Resolves group name → `AbstractGroup` |
| `GroupEditFlagArgument` | `"flag"` | Enum: MIN/MAX_ONLINE_SERVICES, MIN/MAX_MEMORY |
| `ServiceArgument` | `"service"` | Resolves service name → `AbstractService` |
| `IntArgument` | `"amount"` | Integer with optional min/max |
| `TextArgument` | `"value"` | Raw string |
| `StringArrayArgument` | `"command"` | Rest-of-line as string |
| `MemoryArgument` | `"memory"` | Memory value parsing |
| `PlatformArgument` | `"platform"` | Resolves platform by name |
| `PlatformVersionArgument` | `"version"` | Resolves platform version |
| `PlayerArgument` | `"player"` | Resolves player by name |
| `LocaleArgument` | `"locale"` | Locale string |
| `YesNotArgument` | `"confirm"` | Boolean yes/no |

---

## Terminal Architecture (Local runtime only)

```
JLine3Terminal
  ├─ JLine3Reading          — reads input line, dispatches to CommandService
  ├─ CommandService         — command registry + parser
  ├─ SetupController        — interactive setup wizard (OnboardingSetup, GroupSetup, CustomPlatformSetup)
  ├─ ServiceScreenController— screen/log streaming for a service
  └─ JLine3Completer        — tab-completion backed by CommandService
```

Files: `agent/.../terminal/JLine3Terminal.kt`, `JLine3Reading.kt`, `JLine3Completer.kt`

Setup steps: `agent/.../terminal/setup/impl/`  
Logging colours: `agent/.../terminal/LoggingColor.kt`

---

## Runtime System

`Runtime` (abstract) → three implementations, auto-selected at startup:

| Runtime | Detection | Extra features |
|---|---|---|
| `KubernetesRuntime` | Kubernetes env present | fabric8 client |
| `DockerRuntime` | Docker socket accessible | docker-java, mounts socket |
| `LocalRuntime` | Fallback | JLine3 terminal, interactive setup |

Override via env var: `VELOCLOUD_RUNTIME=local|docker|kubernetes`

Each runtime provides: `groupStorage()`, `serviceStorage()`, `factory()`, `expender()`, `templateStorage()`, `configHolder()`.

Runtime code tree: `agent/.../runtime/{local,docker,k8s}/`

---

## Key Subsystems & Where to Find Them

| Subsystem | Path |
|---|---|
| Agent config (`AgentConfig`) | `agent/.../configuration/AgentConfig.kt` |
| gRPC endpoint wiring | `agent/.../grpc/GrpcServerEndpoint.kt` |
| Event bus | `agent/.../events/EventService.kt` + `EventSubscription.kt` |
| Module loader | `agent/.../module/ModuleProvider.kt` |
| Security (group access filter) | `agent/.../security/SecurityProvider.kt` |
| Online state detection | `agent/.../detector/OnlineStateDetector.kt` + `DetectorFactoryThread.kt` |
| Player storage | `agent/.../player/PlayerStorageImpl.kt` |
| Cloud information storage | `agent/.../information/CloudInformationStorageImpl.kt` |
| Shutdown handler | `agent/AgentShutdownHandler.kt` |
| i18n strings | `agent/src/main/resources/i18n/velocloud-agent_<locale>.properties` |
| Logging setup | `agent/.../logging/LoggingAgent.kt` + `LoggingLayout.kt` |
| Utility: port detection | `agent/.../utils/PortDetector.kt` |
| Utility: index detection | `agent/.../utils/IndexDetector.kt` |

---

## Quick grep patterns

```bash
# Find all commands
find agent/src -path "*/commands/impl/*.kt"

# Find where a command is registered
grep -n "registerCommand" agent/src/main/kotlin/de/snenjih/velocloud/agent/runtime/local/LocalRuntime.kt

# Find all i18n keys used in a command
grep -o '"agent\.[^"]*"' agent/src/main/kotlin/de/snenjih/velocloud/agent/runtime/local/terminal/commands/impl/GroupCommand.kt

# Find all argument types
find agent/src -path "*/arguments/type/*.kt"

# Trace a specific i18n key
grep -r "agent.terminal.command.group.list" agent/src/main/resources/
```
