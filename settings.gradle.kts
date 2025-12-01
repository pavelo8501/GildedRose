
pluginManagement {
    plugins {
        kotlin("plugin.serialization")  version(providers.gradleProperty("kotlinVersion").get()) apply false
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "GildedRose"