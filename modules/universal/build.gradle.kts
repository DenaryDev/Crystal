plugins {
    id("crystal.base")
}

base {
    archivesName = "Crystal"
}

dependencies {
    implementation(project(":crystal-core"))
    implementation(project(":crystal-paper"))
    implementation(project(":crystal-velocity"))
}

tasks {
    compileJava {
        dependsOn(":crystal-core:shadowJar")
        dependsOn(":crystal-paper:shadowJar")
        dependsOn(":crystal-velocity:shadowJar")
    }

    shadowJar {
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")

        doLast {
            if (project.hasProperty("universalJarsDestDir")) {
                val destDir = file(project.property("universalJarsDestDir").toString())
                val output = outputs.files.singleFile
                output.copyTo(destDir.resolve(output.name.replace("-all", "")), overwrite = true)
            }
        }
    }
}
