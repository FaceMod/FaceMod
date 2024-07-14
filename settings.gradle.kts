rootProject.name = "FaceMod"

pluginManagement {
    repositories {
        maven("https://junhyung.nexus/")
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)

    repositories {
        maven("https://junhyung.nexus/")
    }
}