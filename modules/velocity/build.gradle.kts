plugins {
    id("crystal.common")
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.runvelocity)
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
    buildConfigField("String", "DESCRIPTION", "\"${project.description}\"")
}

tasks {
    compileJava {
        dependsOn(":crystal-common:shadowJar")
    }

    runVelocity {
        velocityVersion("3.4.0-SNAPSHOT")
        runDirectory(rootProject.projectDir.resolve("run/velocity"))

        val file = rootProject.projectDir.resolve("run/velocity/velocity.jar")
        if (file.exists()) runJar(file)
    }
}
