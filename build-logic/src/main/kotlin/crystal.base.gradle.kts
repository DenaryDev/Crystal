plugins {
    id("java-library")
    id("com.gradleup.shadow")
}

group = providers.gradleProperty("projectGroup").get()
version = providers.gradleProperty("projectVersion").get()
description = providers.gradleProperty("projectDescription").get()

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks {
    shadowJar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        mergeServiceFiles()

        relocate("com.zaxxer.hikari", "me.denarydev.crystal.libs.hikari")

        exclude(
            "org/checkerframework/**",
            "org/slf4j/**",

            "META-INF/maven/**",
            "META-INF/licenses/**",
            "META-INF/LICENSE**",

            "LICENSE",
            "README",
            "INFO_SRC",
            "INFO_BIN",

            "org/sqlite/native/FreeBSD/**",
            "org/sqlite/native/Linux-Android/**",
            "org/sqlite/native/Linux-Musl/**",
            "org/sqlite/native/Linux/arm/**",
            "org/sqlite/native/Linux/armv6/**",
            "org/sqlite/native/Linux/armv7/**",
            "org/sqlite/native/Linux/ppc64/**",
            "org/sqlite/native/Linux/riscv64/**",
            "org/sqlite/native/Linux/x86/**",
            "org/sqlite/native/Mac/x86_64/**",
            "org/sqlite/native/Windows/aarch64/**",
            "org/sqlite/native/Windows/armv7/**",
            "org/sqlite/native/Windows/x86/**"
        )
    }
}
