plugins {
    id("java-library")
    `maven-publish`
}

allprojects {
    apply(plugin = "maven-publish")

    group = "de.snenjih.velocloud"
    version = "3.0.0"

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "velocloud-snapshots"
            url = uri("https://repo.snenjih.de/snapshots")
        }
    }
}