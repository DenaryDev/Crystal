plugins {
    id("crystal.base")
}

base {
    archivesName = "Crystal"
}

val includeCore = providers.gradleProperty("includeCore").map { it.toBoolean() }.orElse(false).get()

dependencies {
    if (includeCore) {
        implementation(project(":crystal-core"))
    }

    implementation(project(":crystal-paper"))
    implementation(project(":crystal-velocity"))
}

tasks {
    val copyUniversalJar = register<Copy>("copyUniversalJar") {
        description = "Copies the universal jar into the directory set by universalJarsDestDir"
        group = "crystal"

        val destinationDir = providers.gradleProperty("universalJarsDestDir")

        onlyIf("universalJarsDestDir is set") { destinationDir.isPresent }

        from(shadowJar)
        into(destinationDir)
    }

    shadowJar {
        archiveClassifier = ""

        destinationDirectory = layout.settingsDirectory.dir("build/libs")

        finalizedBy(copyUniversalJar)
    }
}
