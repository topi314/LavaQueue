rootProject.name = "LavaQueue"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":main")
include(":protocol")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            version("lavalink", "4.0.6")
            version("lavaplayer", "2.2.0")

            library("lavalink-protocol", "dev.arbjerg.lavalink", "protocol").versionRef("lavalink")
            library("lavalink-server", "dev.arbjerg.lavalink", "Lavalink-Server").versionRef("lavalink")

            library("lavaplayer", "dev.arbjerg", "lavaplayer").versionRef("lavaplayer")

            plugin("lavalink", "dev.arbjerg.lavalink.gradle-plugin").version("1.0.14")

            library("annotations", "org.jetbrains", "annotations").version("24.0.1")
            library("kotlin-annotations", "org.jetbrains.kotlin", "kotlin-annotations-jvm").version("1.9.0")
            library("kotlinx-serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json").version("1.5.1")
        }
    }
}
