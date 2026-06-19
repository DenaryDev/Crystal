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

    apiVersion = "1.21"

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
    compileJava {
        dependsOn(":crystal-common:shadowJar")
    }

    runServer {
        minecraftVersion("26.1.2")
        runDirectory(rootProject.projectDir.resolve("run/paper"))

        val file = rootProject.projectDir.resolve("run/paper/paper.jar")
        if (file.exists()) serverJar(file)
    }
}
