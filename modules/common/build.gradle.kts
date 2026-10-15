plugins {
    id("crystal.common")
}

dependencies {
    compileOnlyApi(libs.jspecify)
    api(libs.bundles.configurate)

    implementation(libs.hikaricp)
    implementation(libs.bundles.sql.drivers)

    compileOnly(libs.skinsrestorer)

    testImplementation(libs.adventure.minimessage)
    testImplementation(libs.adventure.legacy)
}

extraJavaModuleInfo {
    automaticModule("com.mysql:mysql-connector-j", "com.mysql")
    automaticModule("net.skinsrestorer:skinsrestorer-api", "skinsrestorer.api")
}
