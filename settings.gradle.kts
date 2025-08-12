@file:Suppress("UnstableApiUsage")

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") { name = "PaperMC" }
        maven("https://repo.prostocraft.ru/public/") { name = "prostocraft" }
    }
}

rootProject.name = "crystal"

includeBuild("build-logic")

submodule("common")
submodule("paper")
submodule("velocity")

fun submodule(name: String) {
    include(name)
    project(":$name").apply {
        this.name = "crystal-$name"
    }
}
