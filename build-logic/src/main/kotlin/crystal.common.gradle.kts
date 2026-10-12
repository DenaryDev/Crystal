import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("crystal.base")
    id("maven-publish")
    id("org.gradlex.extra-java-module-info")
    id("com.diffplug.spotless")
}

extraJavaModuleInfo {
    failOnMissingModuleInfo = false
    skipLocalJars = true
}

spotless {
    java {
        target("**/src/**/java/me/denarydev/crystal/**/*.java")

        @Suppress("UnstableApiUsage")
        licenseHeaderFile(isolated.rootProject.projectDirectory.file("HEADER"))

        endWithNewline()
        trimTrailingWhitespace()
        leadingTabsToSpaces(4)
    }
}

testing {
    suites {
        @Suppress("UnstableApiUsage")
        withType<JvmTestSuite>().configureEach {
            useJUnitJupiter("6.0.1")

            targets.configureEach {
                testTask {
                    testLogging {
                        events(TestLogEvent.PASSED, TestLogEvent.SKIPPED, TestLogEvent.FAILED)
                        showStandardStreams = true
                    }
                }
            }
        }
    }
}

tasks {
    withType<JavaCompile>().configureEach {
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
}

java {
    withSourcesJar()
}

val repositoryProvider: Provider<String> = providers.gradleProperty("projectVersion").map { currentVersion ->
    if (project.name.endsWith("-core")) {
        "private"
    } else if (currentVersion.endsWith("-SNAPSHOT")) {
        "snapshots"
    } else {
        "releases"
    }
}

publishing {
    repositories {
        maven("https://repo.rafaelkauwu.me/${repositoryProvider.get()}/") {
            name = "rafaelkauwu"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
