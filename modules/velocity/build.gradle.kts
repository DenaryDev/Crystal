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
    buildConfigField("VERSION", provider { project.version.toString() })
    buildConfigField("DESCRIPTION", provider { project.description })
}

tasks {
    runVelocity {
        velocityVersion(libs.versions.velocity.get())
        runDirectory = layout.settingsDirectory.dir("run/velocity")

        jvmArgs("--enable-native-access=ALL-UNNAMED")

        val file = layout.settingsDirectory.file("run/velocity/velocity.jar").asFile
        if (file.exists()) runJar(file)
    }
}
