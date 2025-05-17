plugins {
    id("crystal.module")
}

crystalModule {
    name = "utils"
    library = "paper"
}

dependencies {
    compileOnly(libs.paper)
}
