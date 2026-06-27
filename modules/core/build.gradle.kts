plugins {
    id("crystal.common")
}

repositories {
    maven("https://repo.prostocraft.ru/private/") {
        name = "prostocraft"
        credentials(PasswordCredentials::class)
    }
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
    compileJava {
        dependsOn(":crystal-common:shadowJar")
    }

    processResources {
        inputs.properties(
            "version" to project.version,
            "description" to project.description.toString()
        )

        filesMatching("core.yml") {
            expand(
                "version" to project.version,
                "description" to project.description.toString()
            )
        }
    }

    register<JavaExec>("runCore") {
        description = "Runs ProstoCraft Core with this plugin"
        group = "crystal"
        dependsOn(shadowJar)

        val workingDir = rootProject.projectDir.resolve("run/core")
        workingDir(workingDir)

        val coreJar = workingDir.resolve("core.jar")
        if (!coreJar.exists()) {
            throw GradleException("Core jar does not exist: ${coreJar.absolutePath}")
        }

        classpath(coreJar)
        standardInput = System.`in`

        jvmArgs("--enable-native-access=ALL-UNNAMED")

        doFirst {
            if (!workingDir.exists()) {
                workingDir.mkdirs()
            }

            val pluginJar = shadowJar.get().outputs.files.singleFile
            pluginJar.copyTo(workingDir.resolve("plugins/${base.archivesName.get()}-RunCoreTask.jar"), overwrite = true)
        }
    }
}
