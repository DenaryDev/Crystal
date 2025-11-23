plugins {
    id("crystal.common")
}

dependencies {
    compileOnly(libs.core)

    api(project(":crystal-common"))
}

base {
    archivesName = "Crystal-Core"
}

extraJavaModuleInfo {
    automaticModule("ru.prostocraft.core:core", "core")
}

tasks {
    compileJava {
        dependsOn(":crystal-common:shadowJar")
    }

    processResources {
        inputs.property("version", project.version)

        filesMatching("core.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")

        doLast {
            if (project.hasProperty("corePluginsDestDir")) {
                val destDir = file(project.property("corePluginsDestDir").toString())
                val output = outputs.files.singleFile
                output.copyTo(destDir.resolve(output.name), overwrite = true)
            }
        }
    }
}
