plugins {
    id("crystal.module")
}

crystalModule {
    name = "serializers"
    library = "paper"
}

dependencies {
    compileOnly(libs.paper)
    compileOnly(libs.configurate.core)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.mockbukkit)
    testImplementation(libs.configurate.core)
}
