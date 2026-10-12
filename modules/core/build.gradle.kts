plugins {
    id("crystal.common")
}

base {
    archivesName = "Crystal-Core"
}

dependencies {
    api(project(":crystal-common"))
    compileOnlyApi(libs.jspecify)

    compileOnly(libs.core)
}

extraJavaModuleInfo {
    automaticModule("ru.prostocraft.core:core", "core")
}

tasks {
    processResources {
        inputs.properties(
            "version" to provider { project.version.toString() },
            "description" to provider { project.description }
        )

        filesMatching("core.yml") {
            expand(inputs.properties.mapValues { it.value })
        }
    }

    val prepareCoreRun = register<Copy>("prepareCoreRun") {
        description = "Copies this plugin into the ProstoCraft Core run directory"
        group = "crystal"

        val pluginJarName = base.archivesName.map { "$it-RunCoreTask.jar" }

        from(shadowJar)
        into(layout.settingsDirectory.dir("run/core/plugins"))
        rename { pluginJarName.get() }
    }

    register<JavaExec>("runCore") {
        description = "Runs ProstoCraft Core with this plugin"
        group = "crystal"
        dependsOn(prepareCoreRun)

        val runDirectory = layout.settingsDirectory.dir("run/core").asFile
        val coreJar = runDirectory.resolve("core.jar")

        workingDir(runDirectory)
        classpath(coreJar)
        standardInput = System.`in`

        jvmArgs("--enable-native-access=ALL-UNNAMED")

        doFirst {
            if (!coreJar.exists()) {
                throw GradleException("Core jar does not exist: ${coreJar.absolutePath}")
            }
        }
    }
}
