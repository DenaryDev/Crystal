plugins {
    id("crystal.module")
}

crystalModule {
    name = "gui"
    library = "paper"
}

dependencies {
    compileOnly(libs.paper)
}
