plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
    id("org.gradlex.extra-java-module-info")
    id("com.diffplug.spotless")
}

java {
    withSourcesJar()
}

extraJavaModuleInfo {
    failOnMissingModuleInfo = false
    skipLocalJars = true

    automaticModule("io.leangen.geantyref:geantyref", "io.leangen.geantyref")
    automaticModule("com.mysql:mysql-connector-j", "com.mysql")
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

    withType<AbstractPublishToMaven> {
        dependsOn(jar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release = 17
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

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier = ""
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

val repo = if (rootProject.version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"

publishing {
    repositories {
        maven("https://repo.activmine.ru/$repo/") {
            name = "activmine"
            credentials(PasswordCredentials::class)
        }
    }
}
