import net.minecrell.pluginyml.paper.PaperPluginDescription

plugins {
    id("crystal.common")
    id("de.eldoria.plugin-yml.paper") version "0.8.0"
}

base {
    archivesName = "Crystal-Paper"
}

dependencies {
    compileOnly(libs.paper)
    compileOnlyApi(libs.configurate.core)

    api(project(":crystal-common"))

    testImplementation(libs.paper)
    testImplementation(libs.mockbukkit)
}

paper {
    name = "Crystal"
    description = "Набор библиотек для плагинов на платформе Paper"
    author = "DenaryDev"

    main = "me.denarydev.crystal.paper.PaperPlugin"

    apiVersion = "1.21"

    // ConfigLib является частью Crystal
    provides = listOf("ConfigLib")

    serverDependencies {
        register("SkinsRestorer") {
            load = PaperPluginDescription.RelativeLoadOrder.BEFORE
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
        destinationDirectory = rootProject.layout.buildDirectory.dir("libs")

        doLast {
            if (project.hasProperty("paperPluginsDestDir")) {
                val destDir = file(project.property("paperPluginsDestDir").toString())
                val output = outputs.files.singleFile
                output.copyTo(destDir.resolve(output.name.replace("-all", "")), overwrite = true)
            }
        }
    }
}
