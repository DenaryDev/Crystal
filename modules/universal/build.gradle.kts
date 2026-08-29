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
    named<JavaCompile>("compileJava") {
        if (includeCore) {
            dependsOn(":crystal-core:shadowJar")
        }

        dependsOn(":crystal-paper:shadowJar")
        dependsOn(":crystal-velocity:shadowJar")
    }

    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveClassifier = ""

        destinationDirectory = layout.settingsDirectory.dir("build/libs")

        val targetDirProvider = providers.gradleProperty("universalJarsDestDir").map { File(it) }

        doLast {
            if (targetDirProvider.isPresent) {
                val destDir = targetDirProvider.get()
                val output = outputs.files.singleFile

                output.copyTo(destDir.resolve(output.name), overwrite = true)
            }
        }
    }
}
