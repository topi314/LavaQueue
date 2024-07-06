plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

base {
    archivesName = "lavaqueue-protocol"
}

kotlin {
    jvmToolchain(17)
    jvm()
    js(IR) {
        nodejs()
    }

    publishing {
        publications {
            withType<MavenPublication> {
                artifactId = "lavaqueue-$artifactId"
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlinx.serialization)
                implementation(libs.lavalink.protocol)
            }
        }
    }
}
