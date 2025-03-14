import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    id("com.diffplug.spotless") version "7.0.2"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter.engine)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    api(libs.slf4j)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

spotless {
    kotlin { ktfmt("0.54").kotlinlangStyle().configure {

    } }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.register<JavaExec>("generate-mermaid") {
    val output = ByteArrayOutputStream()
    mainClass.set("no.nav.tsm.regulus.regula.generator.GenerateMermaidKt")
    classpath = sourceSets["main"].runtimeClasspath
    group = "documentation"
    description = "Generates mermaid diagram source of rules"
    standardOutput = output
    doLast {
        val readme = File("README.md")
        val lines = readme.readLines()

        val starterTag = "<!-- RULE_MARKER_START -->"
        val endTag = "<!-- RULE_MARKER_END -->"

        val start = lines.indexOfFirst { it.contains(starterTag) }
        val end = lines.indexOfFirst { it.contains(endTag) }

        val newLines: List<String> =
            lines.subList(0, start) +
                    listOf(
                        starterTag,
                    ) +
                    output.toString().split("\n") +
                    listOf(
                        endTag,
                    ) +
                    lines.subList(end + 1, lines.size)
        readme.writeText(newLines.joinToString("\n"))
    }
}
