plugins {
    id("crystal.common")
    id("com.github.gmazzo.buildconfig") version("5.7.0")
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

base {
    archivesName = "Crystal-Velocity"
}

tasks {
    compileJava {
        dependsOn(":crystal-common:shadowJar")
    }

    shadowJar {
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")
    }
}
