plugins {
    id("crystal.module")
}

crystalModule {
    name.set("Crystal Locale")
    moduleName.set("locale")
    description.set("")
    library.set("bukkit")
}

dependencies {
    compileOnly(libs.bukkit)
}
