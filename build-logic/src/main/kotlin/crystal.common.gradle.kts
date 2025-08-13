plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
    id("org.gradlex.extra-java-module-info")
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") { name = "PaperMC" }
    maven("https://repo.codemc.org/repository/maven-public/") { name = "CodeMC" }
    maven("https://repo.prostocraft.ru/public/") { name = "prostocraft" }
}

extraJavaModuleInfo {
    failOnMissingModuleInfo = false
    skipLocalJars = true

    automaticModule("com.mysql:mysql-connector-j", "com.mysql")
    automaticModule("net.skinsrestorer:skinsrestorer-api", "skinsrestorer.api")
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
        options.release = 21
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
    toolchain.languageVersion = JavaLanguageVersion.of(21)

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()
}

val repo = if (rootProject.version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"

publishing {
    repositories {
        maven("https://repo.prostocraft.ru/$repo/") {
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
