rootProject.name = "LavaQueue"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":main")
include(":protocol")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("lavalink", "4.0.6")
            version("lavaplayer", "2.2.0")
            version("kotlin", "2.0.0")

            library("lavalink-protocol", "dev.arbjerg.lavalink", "protocol").versionRef("lavalink")
            library("lavalink-server", "dev.arbjerg.lavalink", "Lavalink-Server").versionRef("lavalink")

            library("lavaplayer", "dev.arbjerg", "lavaplayer").versionRef("lavaplayer")

            plugin("lavalink", "dev.arbjerg.lavalink.gradle-plugin").version("1.0.14")
            plugin("kotlin-jvm", "org.jetbrains.kotlin.jvm").versionRef("kotlin")
            plugin("kotlin-multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef("kotlin")
            plugin("kotlin-serialization", "org.jetbrains.kotlin.plugin.serialization").versionRef("kotlin")
            
            library("kotlinx-serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.7.1")
        }
    }
}
