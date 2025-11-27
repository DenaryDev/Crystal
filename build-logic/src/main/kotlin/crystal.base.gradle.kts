plugins {
    id("java-library")
    id("com.gradleup.shadow")
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

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
}
