plugins {
    id("crystal.common")
    id("de.eldoria.plugin-yml.paper") version "0.7.1"
}

base {
    archivesName = "Crystal-Paper"
}

dependencies {
    compileOnly(libs.paper)
    compileOnlyApi(libs.configurate.core)
    compileOnlyApi(libs.configlib)

    api(project(":crystal-common"))

    testImplementation(libs.mockbukkit)
    testImplementation(libs.junit.jupiter)
    testImplementation(platform(libs.junit.bom))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.12.2")
}

paper {
    name = "Crystal"
    description = "Набор библиотек для плагинов на платформе Paper"
    author = "DenaryDev"

    main = "me.denarydev.crystal.paper.PaperPlugin"

    apiVersion = "1.21"

    serverDependencies {
        register("ConfigLib") {
            required = false
        }
    }
}

tasks {
    compileJava {
        dependsOn(":crystal-common:shadowJar")
    }

    shadowJar {
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")
    }
}
