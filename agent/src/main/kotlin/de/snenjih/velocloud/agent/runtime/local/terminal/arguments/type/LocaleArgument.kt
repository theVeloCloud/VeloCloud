package de.snenjih.velocloud.agent.runtime.local.terminal.arguments.type

import de.snenjih.velocloud.agent.i18n
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.InputContext
import de.snenjih.velocloud.agent.runtime.local.terminal.arguments.TerminalArgument
import java.util.Locale

class LocaleArgument(key: String = "locale") : TerminalArgument<Locale>(key) {

    override fun buildResult(input: String, context: InputContext): Locale {
        return i18n.SUPPORTED_LOCALES.find { input.equals(it.toLanguageTag(), ignoreCase = true) }
            ?: Locale.ENGLISH // TODO
    }

    override fun defaultArgs(context: InputContext): MutableList<String> {
        return i18n.SUPPORTED_LOCALES.map { it.toLanguageTag() }.sorted().toMutableList()
    }

    override fun wrongReason(rawInput: String): String {
        return i18n.get("agent.terminal.command.argument.type.locale.wrong", rawInput)
    }

    override fun predication(rawInput: String): Boolean {
        return i18n.SUPPORTED_LOCALES.any { rawInput.equals(it.toLanguageTag(), ignoreCase = true) }
    }


}