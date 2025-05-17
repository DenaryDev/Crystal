import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

dependencies {
    implementation("com.gradleup.shadow:shadow-gradle-plugin:8.3.6")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:7.0.3")
    implementation("org.gradlex:extra-java-module-info:1.12")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(17)
}

kotlin {
    jvmToolchain(17)
}

tasks {
    withType<JavaCompile>().configureEach {
        options.release.set(17)
    }

    withType<KotlinCompile>().configureEach {
        compilerOptions {
            jvmTarget = JvmTarget.JVM_17
        }
    }
}
