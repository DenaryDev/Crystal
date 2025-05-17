plugins {
    id("crystal.module")
}

crystalModule {
    name = "config"
    library = "shared"
}

dependencies {
    compileOnlyApi(libs.annotations)
    api(libs.bundles.configurate)
}
