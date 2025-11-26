plugins {
    id("crystal.common")
    id("com.github.gmazzo.buildconfig") version("5.7.1")
}

base {
    archivesName = "Crystal-Velocity"
}

dependencies {
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)

    api(project(":crystal-common"))
}

buildConfig {
    packageName("me.denarydev.crystal.velocity")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
}

tasks {
    compileJava {
        dependsOn(":crystal-common:shadowJar")
    }

    shadowJar {
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")

        doLast {
            if (project.hasProperty("velocityPluginsDestDir")) {
                val destDir = file(project.property("velocityPluginsDestDir").toString())
                val output = outputs.files.singleFile
                output.copyTo(destDir.resolve(output.name.replace("-all", "")), overwrite = true)
            }
        }
    }
}
