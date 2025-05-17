import crystal.extension.CrystalLibraryExtension

plugins {
    id("java-library")
    id("maven-publish")
    id("crystal.common")
}

val extension = project.extensions.create("crystalLibrary", CrystalLibraryExtension::class, project)

afterEvaluate {
    // Print the version and throw an exception if it doesn't match
    if (version != rootProject.version) {
        throw GradleException("Library ${extension.name.get()} version ($version) does not match root project version (${rootProject.version}). Do not change it!")
    }
}

tasks {
    // A library isn't truly a distributed artifact, rather it is just maven metadata to depend on the modules of a library.
    // For that reason, we have no use for the jar or remapJar tasks.
    jar {
        enabled = false
    }
}
