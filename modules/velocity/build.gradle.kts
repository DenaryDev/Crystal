plugins {
    id("crystal.common")
    alias(libs.plugins.buildconfig)
    alias(libs.plugins.runvelocity)
}

base {
    archivesName = "Crystal-Velocity"
}

dependencies {
    api(project(":crystal-common"))

    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)
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
        velocityVersion(libs.versions.velocity.get())
        runDirectory(rootProject.projectDir.resolve("run/velocity"))

        jvmArgs("--enable-native-access=ALL-UNNAMED")

        val file = rootProject.projectDir.resolve("run/velocity/velocity.jar")
        if (file.exists()) runJar(file)
    }
}
