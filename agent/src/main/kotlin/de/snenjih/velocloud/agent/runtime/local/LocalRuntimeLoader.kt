package de.snenjih.velocloud.agent.runtime.local

import de.snenjih.velocloud.agent.runtime.RuntimeLoader

class LocalRuntimeLoader : RuntimeLoader {

    override fun runnable(): Boolean {
        return true
    }

    override fun instance(): LocalRuntime {
       return LocalRuntime()
    }
}