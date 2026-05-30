plugins {
    kotlin("jvm") version "2.3.0"
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation(projects.common)
    testImplementation(libs.velocloud.proto)

    compileOnly(libs.bundles.confirationPool)

    compileOnly(libs.log4j.api)
    compileOnly(libs.velocloud.proto)
    compileOnly(project(":common"))
}

val copyTasks by tasks.registering(Copy::class) {
    from("../metadata/tasks").into("$projectDir/src/main/resources/metadata/tasks")
}

val copyPlatforms by tasks.registering(Copy::class) {
    from("../metadata/platforms").into("$projectDir/src/main/resources/metadata/platforms")
}

tasks.named("processResources") {
    dependsOn(copyTasks, copyPlatforms)
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.jar {
    archiveFileName.set("velocloud-platforms-$version.jar")
}