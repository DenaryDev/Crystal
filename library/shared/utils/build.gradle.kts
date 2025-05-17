plugins {
    id("crystal.module")
}

crystalModule {
    name = "utils"
    library = "shared"
}

dependencies {
    compileOnlyApi(libs.annotations)
}
