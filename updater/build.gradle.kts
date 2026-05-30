plugins {
    kotlin("jvm") version "2.3.0"
}

dependencies {
    testImplementation(kotlin("test"))
    compileOnly(projects.common)
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "de.snenjih.velocloud.updater.UpdaterRuntime",
            "VELOCLOUD_VERSION" to version
        )
    }
    archiveFileName.set("velocloud-updater-$version.jar")
}

kotlin {
    jvmToolchain(21)
}