plugins {
    id("crystal.common")
}

base {
    archivesName = "Crystal-Core"
}

dependencies {
    compileOnly(libs.core)

    api(project(":crystal-common"))
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
}
