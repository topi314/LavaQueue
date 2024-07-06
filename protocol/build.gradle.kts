plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
}

base {
    archivesName = "lavaqueue-protocol"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
        }
    }
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
