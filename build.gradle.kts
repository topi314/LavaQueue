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
        maven("https://maven.arbjerg.dev/releases")
        maven("https://maven.arbjerg.dev/snapshots")
        maven("https://jitpack.io")
        jcenter()
    }
}

val isMavenDefined = findProperty("MAVEN_USERNAME") != null && findProperty("MAVEN_PASSWORD") != null
subprojects {
    apply<MavenPublishPlugin>()

    configure<PublishingExtension> {
        if (findProperty("MAVEN_PASSWORD") != null && findProperty("MAVEN_USERNAME") != null) {
            repositories {
                val snapshots = "https://maven.lavalink.dev/snapshots"
                val releases = "https://maven.lavalink.dev/releases"

                maven(if (release) releases else snapshots) {
                    credentials {
                        password = findProperty("MAVEN_PASSWORD") as String?
                        username = findProperty("MAVEN_USERNAME") as String?
                    }
                }
            }
        } else {
            logger.lifecycle("Not publishing to maven.lavalink.dev because credentials are not set")
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
        return Pair(versionStr.toString().trim(), false)
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

    return Pair(versionStr.toString().trim(), true)
}
