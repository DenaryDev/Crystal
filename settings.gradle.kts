pluginManagement {
    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "crystal"

val includeCore = providers.gradleProperty("includeCore").map { it.toBoolean() }.orElse(false).get()

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc" }
        maven("https://repo.codemc.org/repository/maven-public/") { name = "codemc" }
        maven("https://repo.rafaelkauwu.me/public/") { name = "rafaelkauwu-public" }

        if (includeCore) {
            maven("https://repo.rafaelkauwu.me/private/") {
                name = "rafaelkauwu"
                credentials(PasswordCredentials::class)
            }
        }
    }
}

submodule("common")
submodule("paper")
submodule("velocity")

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
