plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
    id("org.gradlex.extra-java-module-info")
    id("com.diffplug.spotless")
}

extraJavaModuleInfo {
    failOnMissingModuleInfo = false
    skipLocalJars = true

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

    compileJava {
        dependsOn(spotlessApply)

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
        archiveClassifier = "fat"
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }

    test {
        useJUnitPlatform()
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
}

val repo = if (rootProject.version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"

publishing {
    repositories {
        maven("https://repo.activmine.ru/$repo/") {
            name = "activmine"
            credentials(PasswordCredentials::class)
        }
    }

    publications {
        register<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
