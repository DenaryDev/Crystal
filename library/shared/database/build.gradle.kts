plugins {
    id("crystal.module")
}

crystalModule {
    name = "database"
    library = "shared"
}

dependencies {
    compileOnlyApi(libs.annotations)
    api(libs.hikaricp)
    api(libs.bundles.sql.drivers)
}
