plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.gradle.shadow)
    implementation(libs.gradle.spotless)
    implementation(libs.gradle.extrajavamoduleinfo)
}

kotlin {
    jvmToolchain(25)
}
