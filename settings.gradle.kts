@file:Suppress("UnstableApiUsage")

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
