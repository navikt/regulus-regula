import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.kotlin.jvm)
    `java-library`
    id("com.diffplug.spotless") version "7.0.2"
    id("maven-publish")
}

repositories {
    mavenCentral()
    maven { url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release") }
}

dependencies {
    testImplementation(libs.junit.jupiter.engine)
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.slf4j:slf4j-nop:2.0.9")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    api(libs.diagnosekoder)
    api(libs.slf4j)
}

java { toolchain { languageVersion = JavaLanguageVersion.of(21) } }

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["kotlin"])
            groupId = "no.nav.tsm.regulus"
            artifactId = "regula"
            version = file("version").readText().trim()
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/navikt/regulus-regula")
            credentials {
                username = "x-access-token"
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}

spotless { kotlin { ktfmt("0.54").kotlinlangStyle() } }

tasks.named<Test>("test") { useJUnitPlatform() }

tasks.register<JavaExec>("lintTrees") {
    logging.captureStandardOutput(LogLevel.LIFECYCLE)

    mainClass.set("no.nav.tsm.regulus.regula.meta.TreeLinterKt")
    classpath = sourceSets["main"].runtimeClasspath
    group = "documentation"
    description = "Validate tree implementation structure"

    environment("ONLY_ERRORS", true)
}

tasks.register<JavaExec>("generateMermaid") {
    val output = ByteArrayOutputStream()
    mainClass.set("no.nav.tsm.regulus.regula.meta.GenerateMermaidKt")
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
                listOf(starterTag) +
                output.toString().split("\n") +
                listOf(endTag) +
                lines.subList(end + 1, lines.size)
        readme.writeText(newLines.joinToString("\n"))
    }
}

tasks.named("build") {
    dependsOn("lintTrees")
    dependsOn("generateMermaid")
}
