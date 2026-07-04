plugins {
    id("crystal.base")
    id("maven-publish")
    id("org.gradlex.extra-java-module-info")
    id("com.diffplug.spotless")
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(platform("org.junit:junit-bom:6.0.1"))
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

extraJavaModuleInfo {
    failOnMissingModuleInfo = false
    skipLocalJars = true
}

spotless {
    java {
        target("**/me/denarydev/crystal/**")

        licenseHeaderFile(rootProject.file("HEADER"))

        endWithNewline()
        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
    }
}

tasks {
    assemble {
        dependsOn(spotlessCheck)
    }

    compileJava {
        dependsOn(spotlessApply)

        options.encoding = Charsets.UTF_8.name()
        options.release = 25
        options.compilerArgs.addAll(
            listOf(
                "-parameters",
                "-nowarn",
                "-Xlint:-unchecked",
                "-Xlint:-deprecation",
                "-Xlint:-processing"
            )
        )
        options.isFork = true
    }

    test {
        useJUnitPlatform()

        testLogging {
            events("passed", "skipped", "failed")
            showStandardStreams = true
        }
    }
}

java {
    withSourcesJar()
}

val repository: String = if (project.name.endsWith("-core")) {
    "private"
} else if (rootProject.version.toString().endsWith("-SNAPSHOT")) {
    "snapshots"
} else {
    "releases"
}

publishing {
    repositories {
        maven("https://repo.prostocraft.ru/$repository/") {
            name = "prostocraft"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
