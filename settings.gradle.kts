plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "crystal"

includeBuild("build-logic")

submodule("common")
submodule("paper")
submodule("velocity")

val includeCore = providers.gradleProperty("includeCore").map { it.toBoolean() }.orElse(false).get()
if (includeCore) {
    submodule("core")
}

submodule("universal")

fun submodule(name: String) {
    include(name)
    project(":$name").apply {
        this.projectDir = file("modules/$name")
        this.name = "crystal-$name"
    }
}
