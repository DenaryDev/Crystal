plugins {
    id("crystal.common")
}

dependencies {
    compileOnlyApi(libs.annotations)
    api(libs.bundles.configurate)
    api(libs.hikaricp)
    api(libs.bundles.sql.drivers)
}
