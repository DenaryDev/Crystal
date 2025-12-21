plugins {
    id("crystal.common")
}

dependencies {
    compileOnlyApi(libs.annotations)
    api(libs.bundles.configurate)

    implementation(libs.hikaricp)
    implementation(libs.bundles.sql.drivers)

    compileOnly(libs.skinsrestorer)
}

extraJavaModuleInfo {
    automaticModule("com.mysql:mysql-connector-j", "com.mysql")
    automaticModule("net.skinsrestorer:skinsrestorer-api", "skinsrestorer.api")
}
