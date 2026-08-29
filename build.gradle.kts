plugins {
    alias(libs.plugins.spotless) apply false
}

//spotless {
//    java {
//        val rootDir = isolated.projectDirectory
//
//        licenseHeaderFile(rootDir.file("HEADER"))
//
//        target(fileTree(rootDir) {
//            include("**/src/main/java/me/denarydev/crystal/**/*.java")
//        })
//
//        endWithNewline()
//        trimTrailingWhitespace()
//        leadingTabsToSpaces(4)
//    }
//}
