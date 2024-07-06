import java.io.ByteArrayOutputStream

plugins {
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "1.9.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" apply false
}

val (gitVersion, release) = versionFromGit()
logger.lifecycle("Version: $gitVersion (release: $release)")

allprojects {
    group = "com.github.topi314.lavaqueue"

    version = gitVersion

    repositories {
        mavenLocal()
        mavenCentral()
        maven("https://maven.lavalink.dev/releases")
        maven("https://maven.lavalink.dev/snapshots")
        maven("https://jitpack.io")
        jcenter()
    }
}

val isMavenDefined = findProperty("MAVEN_USERNAME") != null && findProperty("MAVEN_PASSWORD") != null
val isLavalinkMavenDefined = findProperty("LAVALINK_MAVEN_USERNAME") != null && findProperty("LAVALINK_MAVEN_PASSWORD") != null
subprojects {
    apply<MavenPublishPlugin>()

    configure<PublishingExtension> {
        if (isMavenDefined) {
            repositories {
                val snapshots = "https://maven.topi.wtf/snapshots"
                val releases = "https://maven.topi.wtf/releases"

                maven(if (release) releases else snapshots) {
                    credentials {
                        password = findProperty("MAVEN_PASSWORD") as String?
                        username = findProperty("MAVEN_USERNAME") as String?
                    }
                }
            }
        }
        if (isLavalinkMavenDefined && name == "main") {
            repositories {
                val snapshots = "https://maven.lavalink.dev/snapshots"
                val releases = "https://maven.lavalink.dev/releases"

                maven(if (release) releases else snapshots) {
                    credentials {
                        password = findProperty("LAVALINK_MAVEN_PASSWORD") as String?
                        username = findProperty("LAVALINK_MAVEN_USERNAME") as String?
                    }
                }
            }
        }
    }
}

fun versionFromGit(): Pair<String, Boolean> {
    var versionStr = ByteArrayOutputStream()
    var result = exec {
        standardOutput = versionStr
        isIgnoreExitValue = true
        commandLine = listOf("git", "describe", "--exact-match", "--tags")
    }
    if (result.exitValue == 0) {
        return Pair(versionStr.toString().trim(), true)
    }

    versionStr = ByteArrayOutputStream()
    result = exec {
        standardOutput = versionStr
        isIgnoreExitValue = true
        commandLine = listOf("git", "rev-parse", "--short", "HEAD")
    }
    if (result.exitValue != 0) {
        throw GradleException("Failed to get git version")
    }

    return Pair(versionStr.toString().trim(), false)
}
