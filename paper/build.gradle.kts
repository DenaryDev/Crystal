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

    api(libs.configlib.core)
    api(libs.configlib.yaml) {
        isTransitive = false
    }

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
        register("SkinsRestorer") {
            required = false
        }
    }
}

extraJavaModuleInfo {
    automaticModule("de.exlll:configlib-core", "configlib") {
        mergeJar("de.exlll:configlib-yaml")
    }
}

tasks {
    compileJava {
        dependsOn(":crystal-common:shadowJar")
    }

    shadowJar {
        archiveClassifier = ""
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")
    }
}
