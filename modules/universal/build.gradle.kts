plugins {
    id("crystal.base")
}

base {
    archivesName = "Crystal"
}

dependencies {
    findProject(":crystal-core")?.also { implementation(it) }
    implementation(project(":crystal-paper"))
    implementation(project(":crystal-velocity"))
}

tasks {
    compileJava {
        if (findProject(":crystal-core") != null) dependsOn(":crystal-core:shadowJar")
        dependsOn(":crystal-paper:shadowJar")
        dependsOn(":crystal-velocity:shadowJar")
    }

    shadowJar {
        archiveClassifier = ""
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")

        doLast {
            if (project.hasProperty("universalJarsDestDir")) {
                val destDir = file(project.property("universalJarsDestDir").toString())
                val output = outputs.files.singleFile
                output.copyTo(destDir.resolve(output.name), overwrite = true)
            }
        }
    }
}
