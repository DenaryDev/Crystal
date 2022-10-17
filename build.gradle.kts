plugins {
    `maven-publish`
}

allprojects {
    group = "io.sapphiremc"
    version = "0.1.0-SNAPSHOT"
}

publishing {
    publications.create<MavenPublication>("maven") {
        pom.withXml {
            val depsNode = asNode().appendNode("dependencies")

            rootProject.subprojects.stream().filter {
                it.path.split(":").size == 2
            }.forEach {
                val depNode = depsNode.appendNode("dependency")
                depNode.appendNode("groupId", it.group)
                depNode.appendNode("artifactId", it.name)
                depNode.appendNode("version", it.version)
                depNode.appendNode("scope", "compile")
            }
        }
    }
}