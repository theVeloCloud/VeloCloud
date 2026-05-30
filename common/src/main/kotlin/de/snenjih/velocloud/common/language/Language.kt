package de.snenjih.velocloud.common.language

import de.snenjih.velocloud.common.os.currentOS

enum class Language(private var fileSuffix: String?, val nativeExecutable: Boolean) {

    JAVA("jar", false),
    GO(null, true),
    RUST(null, true);

    fun suffix(): String {
        val suffix = if (nativeExecutable) currentOS.nativeExecutableSuffix else fileSuffix

        return if (suffix != null) ".$suffix" else ""
    }
}