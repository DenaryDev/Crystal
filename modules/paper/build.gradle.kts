import net.minecrell.pluginyml.paper.PaperPluginDescription
import xyz.jpenilla.runpaper.task.RunServer

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
    named<JavaCompile>("compileJava") {
        dependsOn(":crystal-common:shadowJar")
    }

    named<RunServer>("runServer") {
        minecraftVersion("26.1.2")
        runDirectory(rootProject.projectDir.resolve("run/paper"))

        val file = rootProject.projectDir.resolve("run/paper/paper.jar")
        if (file.exists()) serverJar(file)
    }
}
