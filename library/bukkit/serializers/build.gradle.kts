plugins {
    id("io.papermc.paperweight.userdev") version "1.5.4"
}

crystalModule {
    name.set("Crystal Serializers")
    moduleName.set("serializers")
    description.set("Configurate serializers for some bukkit objects")
    library.set("bukkit")
}

repositories {
    maven("https://the-planet.fun/repo/snapshots")
}

dependencies {
    paperweight.devBundle("io.sapphiremc.sapphire", libs.bukkit.get().version)
    implementation(project(":bukkit:nms"))
    compileOnly(libs.bukkit)
    compileOnly(libs.configurate)
}

tasks {
    build {
        dependsOn(reobfJar)
    }

    withType<AbstractPublishToMaven> {
        dependsOn(reobfJar)
    }
}

afterEvaluate {
    publishing {
        publications.create<MavenPublication>("reobf") {
            artifact(tasks.reobfJar.get().outputJar.get())
        }
    }
}
