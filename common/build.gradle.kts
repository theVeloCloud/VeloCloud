plugins {
    kotlin("jvm") version "2.3.0"
}

dependencies {
    implementation("com.google.guava:guava:33.5.0-jre")
    testImplementation(kotlin("test"))
    api(libs.gson)
    compileOnly(libs.velocloud.proto)
    compileOnly(libs.log4j.api)
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

tasks.jar {
    archiveFileName.set("velocloud-common-$version.jar")
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            artifact(tasks.jar.get())

            pom {
                description.set("VeloCloud gRPC API with bundled dependencies")
                url.set("https://github.com/theVeloCloud/velocloud")

                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                developers {
                    developer {
                        name.set("Mirco Lindenau")
                        email.set("mirco.lindenau@gmx.de")
                    }
                }
                scm {
                    url.set("https://github.com/theVeloCloud/velocloud")
                    connection.set("scm:git:https://github.com/theVeloCloud/velocloud.git")
                    developerConnection.set("scm:git:https://github.com/theVeloCloud/velocloud.git")
                }
            }
        }
    }
}
