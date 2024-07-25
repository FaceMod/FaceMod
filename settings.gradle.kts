rootProject.name = "FaceMod"

pluginManagement {
    repositories {
        maven("https://junhyung.nexus/")
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)

    repositories {
        maven("https://junhyung.nexus/")
        mavenCentral()
        maven { url = uri("https://maven.fabricmc.net/") }
    }
}