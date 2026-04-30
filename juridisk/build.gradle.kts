plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    id("com.diffplug.spotless") version "7.0.2"
    id("maven-publish")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")

    api(libs.diagnosekoder)
    api(libs.slf4j)
    api(libs.prometheus)
}

spotless { kotlin { ktfmt("0.54").kotlinlangStyle() } }
