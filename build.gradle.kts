plugins {
    id("java-library")
    `maven-publish`
}

allprojects {
    apply(plugin = "maven-publish")

    group = "de.snenjih.velocloud"
    version = "3.0.3"

    repositories {
        mavenLocal()
        mavenCentral()

        maven {
            name = "velocloud-snapshots"
            url = uri("https://repo.snenjih.de/snapshots")
            credentials {
                username = System.getenv("REPOSILITE_USER") ?: ""
                password = System.getenv("REPOSILITE_SECRET") ?: ""
            }
        }
    }
}