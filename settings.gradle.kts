plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "crystal"

includeBuild("build-logic")

submodule("common")
submodule("paper")
submodule("velocity")
submodule("core")
submodule("universal")

fun submodule(name: String) {
    include(name)
    project(":$name").apply {
        this.projectDir = file("modules/$name")
        this.name = "crystal-$name"
    }
}

@Suppress("UnstableApiUsage")
gradle.lifecycle.beforeProject {
    val baseVersion = providers.gradleProperty("baseVersion").get().trim()
    val buildNumber = providers.environmentVariable("GITHUB_RUN_NUMBER").orNull?.trim()?.toInt()
    val versionString = if (buildNumber == null) {
        "$baseVersion.local-SNAPSHOT"
    } else {
        "$baseVersion.build.$buildNumber"
    }

    version = versionString
}
