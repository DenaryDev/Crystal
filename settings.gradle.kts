@file:Suppress("UnstableApiUsage")

import java.nio.file.Files

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") { name = "PaperMC" }
        maven("https://repo.activmine.ru/public") { name = "ActivMine" }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "crystal"

includeBuild("build-logic")

library("paper")
library("shared")

platform("paper")
platform("velocity")

fun library(library: String) {
    include(library)

    val libraryProject = project(":$library")
    libraryProject.projectDir = file("library/$library")

    rootProject.projectDir.toPath().resolve("library/$library/").toFile().listFiles()?.forEach {
        // Is the module disabled?
        if (it.isDirectory
            && it.name != "src" // Ignore sources
            && it.name != "build" // Ignore build artifacts
            && !it.name.startsWith(".") // Ignore anything hidden on unix-like OSes
        ) {
            // Libraries can be disabled by adding a file named DISABLE at the root of its directory
            if (Files.exists(it.toPath().resolve("build.gradle.kts")) && Files.notExists(it.toPath().resolve("DISABLE"))) {
                include("$library:${it.name}")
            }
        }
    }
}

fun platform(platform: String) {
    include(platform)

    val platformProject = project(":$platform")
    platformProject.projectDir = file("platform/$platform")
}
