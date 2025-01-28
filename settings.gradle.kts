rootProject.name = "FaceMod"

pluginManagement {
    repositories {
        maven("https://junhyung.nexus/")
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.shedaniel.me/") }
        maven { url = uri("https://api.modrinth.com/maven") }
        maven { url = uri("https://maven.terraformersmc.com/releases/") }
        maven { url = uri("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") }
        gradlePluginPortal()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositories {
        maven("https://junhyung.nexus/")
        maven { url = uri("https://maven.fabricmc.net/") }
        maven { url = uri("https://maven.shedaniel.me/") }
        maven { url = uri("https://api.modrinth.com/maven") }
        maven { url = uri("https://maven.terraformersmc.com/releases/") }
        maven { url = uri("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") }
        mavenCentral()
    }
}