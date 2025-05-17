plugins {
    id("crystal.common")
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
}

dependencies {
    compileOnly(libs.paper)

    api(projects.paper.gui)
    api(projects.paper.serializers)
    api(projects.paper.utils)

    api(projects.shared.config)
    api(projects.shared.database)
    api(projects.shared.utils)
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
    shadowJar {
        archiveClassifier = ""
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")
    }
}
