plugins {
    id("crystal.common")
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
}

dependencies {
    compileOnly(libs.paper)
    compileOnlyApi(libs.configurate.core)

    api(project(":crystal-common"))
}

bukkit {
    name = "Crystal"
    description = "Набор библиотек для плагинов на платформе Paper"
    author = "DenaryDev"

    main = "me.denarydev.crystal.platform.paper.PaperPlugin"

    apiVersion = "1.18"
}

base {
    archivesName = "Crystal-Paper"
}

tasks {
    compileJava {
        dependsOn(":crystal-common:shadowJar")
    }

    shadowJar {
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")
    }
}
