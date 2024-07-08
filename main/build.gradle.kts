plugins {
    kotlin("jvm")
    alias(libs.plugins.lavalink)
}

base {
    archivesName = "lavaqueue-plugin"
}

lavalinkPlugin {
    name = "lavaqueue-plugin"
    apiVersion = libs.versions.lavalink
    serverVersion = libs.versions.lavalink
    configurePublishing = false
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.protocol)
    compileOnly(libs.lavalink.server)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifactId = base.archivesName.get()
        }
    }
}

