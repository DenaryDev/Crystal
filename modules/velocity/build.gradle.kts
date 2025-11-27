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
}
