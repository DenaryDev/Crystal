import crystal.extension.CrystalModuleExtension

plugins {
    id("java-library")
    id("maven-publish")
    id("crystal.common")
}

val extension = project.extensions.create("crystalModule", CrystalModuleExtension::class, project)

// We need to wait until project evaluation has finished to peek into a complete Crystal module extension.
afterEvaluate {
    if (extension.library == null) {
        throw GradleException("Module ${extension.name.get()} has an invalid parent project that doesn't define its `libraryName` field.")
    }

    group = "${rootProject.group}.${extension.library.get()}"

    publishing {
        publications.create<MavenPublication>("shadow") {
            artifact(tasks["shadowJar"])
            artifact(tasks["sourcesJar"])
        }
    }

    tasks.test {
        useJUnitPlatform()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
}
