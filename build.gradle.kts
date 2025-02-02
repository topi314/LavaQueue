import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
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
                        username = findProperty("MAVEN_USERNAME") as String?
                        password = findProperty("MAVEN_PASSWORD") as String?
                    }
                }
            }
        } else {
            logger.lifecycle("Not publishing to maven.topi.wtf because credentials are not set")
        }

        if (name == "main") {
            if (isLavalinkMavenDefined) {
                repositories {
                    val snapshots = "https://maven.lavalink.dev/snapshots"
                    val releases = "https://maven.lavalink.dev/releases"

                    maven(if (release) releases else snapshots) {
                        credentials {
                            username = findProperty("LAVALINK_MAVEN_USERNAME") as String?
                            password = findProperty("LAVALINK_MAVEN_PASSWORD") as String?
                        }
                    }
                }
            } else {
                logger.lifecycle("Not publishing to maven.lavalink.dev because credentials are not set")
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
