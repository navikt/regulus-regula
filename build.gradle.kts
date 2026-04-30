import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.jvm.tasks.Jar
import org.gradle.api.tasks.testing.Test

plugins {
    base
}

val projectVersion = rootProject.file("gradle/version").readText().trim()

subprojects {
    group = "no.nav.tsm.regulus"
    version = projectVersion

    repositories {
        mavenCentral()
        maven { url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release") }
    }

    pluginManager.withPlugin("java-library") {
        extensions.configure<JavaPluginExtension>("java") {
            toolchain { languageVersion = JavaLanguageVersion.of(21) }
            withSourcesJar()
        }
    }

    tasks.withType<Jar>().configureEach {
        manifest { attributes["Implementation-Version"] = project.version.toString() }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    pluginManager.withPlugin("maven-publish") {
        extensions.configure<PublishingExtension>("publishing") {
            publications {
                create<MavenPublication>("gpr") {
                    from(components["java"])
                    groupId = project.group.toString()
                    artifactId = project.name
                    version = project.version.toString()
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
    }
}

tasks.register("publishAll") {
    group = "publishing"
    description = "Publishes juridisk and regula to GitHub Packages."
    dependsOn(
        ":juridisk:publishGprPublicationToGitHubPackagesRepository",
        ":regula:publishGprPublicationToGitHubPackagesRepository",
    )
}

tasks.register("publishAllToMavenLocal") {
    group = "publishing"
    description = "Publishes juridisk and regula to the local Maven cache."
    dependsOn(
        ":juridisk:publishToMavenLocal",
        ":regula:publishToMavenLocal",
    )
}
