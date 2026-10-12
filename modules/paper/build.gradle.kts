import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("crystal.common")
    alias(libs.plugins.pluginyml)
    alias(libs.plugins.runpaper)
}

base {
    archivesName = "Crystal-Paper"
}

dependencies {
    api(project(":crystal-common"))

    compileOnly(libs.paper)
    compileOnlyApi(libs.configurate.core)

    testImplementation(libs.paper)
    testImplementation(libs.mockbukkit)
}

paper {
    name = "Crystal"
    author = "DenaryDev"

    main = "me.denarydev.crystal.paper.PaperPlugin"

    apiVersion = "26.2"

    serverDependencies {
        register("SkinsRestorer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
            required = false
        }
    }
}

extraJavaModuleInfo {
    automaticModule("com.mojang:brigadier", "com.mojang.brigadier")
}

tasks {
    runServer {
        minecraftVersion("26.1.2")
        runDirectory = layout.settingsDirectory.dir("run/paper")

        val file = layout.settingsDirectory.file("run/paper/paper.jar").asFile
        if (file.exists()) serverJar(file)
    }
}
