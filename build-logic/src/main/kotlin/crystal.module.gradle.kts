import crystal.extension.CrystalModuleExtension

plugins {
    id("crystal.base")
    `maven-publish`
}

val extension = project.extensions.create("crystalModule", CrystalModuleExtension::class, project)

afterEvaluate {
    group = "io.sapphiremc.crystal.${extension.library.get()}"

    java {
        withSourcesJar()
        withJavadocJar()
    }

    publishing {
        publications.create<MavenPublication>("shadow") {
            project.shadow.component(this)
            artifact(tasks["sourcesJar"])
            artifact(tasks["javadocJar"])
        }
    }
}
