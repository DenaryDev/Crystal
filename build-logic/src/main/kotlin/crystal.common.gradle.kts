plugins {
    id("java-library")
    id("maven-publish")
    id("com.gradleup.shadow")
    id("org.gradlex.extra-java-module-info")
    id("com.diffplug.spotless")
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") { name = "papermc" }
    maven("https://repo.codemc.org/repository/maven-public/") { name = "codemc" }
    maven("https://repo.prostocraft.ru/private/") {
        name = "prostocraft"
        credentials(PasswordCredentials::class)
    }
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
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
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
    toolchain.languageVersion = JavaLanguageVersion.of(21)

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21

    withSourcesJar()
}

publishing {
    repositories {
        maven("https://repo.prostocraft.ru/private/") {
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
