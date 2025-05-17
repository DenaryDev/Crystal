plugins {
    id("crystal.common")
    id("com.github.gmazzo.buildconfig") version("5.6.5")
}

dependencies {
    compileOnly(libs.velocity)
    annotationProcessor(libs.velocity)

    api(projects.shared.config)
    api(projects.shared.database)
    api(projects.shared.utils)
}

buildConfig {
    packageName("me.denarydev.crystal.platform.velocity")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
}

base {
    archivesName = "Crystal-Velocity"
}

tasks {
    shadowJar {
        archiveClassifier = ""
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")
    }
}
