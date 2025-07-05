plugins {
    `java-library`
    id("fabric-loom") version "1.8.11"
}

tasks.withType<net.fabricmc.loom.task.AbstractRemapJarTask> {
    archiveVersion = ""
}

group = "main.java.io.github.facemod"
version = "1.0.0"

dependencies {
    minecraft("com.mojang:minecraft:1.21.4")
    mappings("net.fabricmc:yarn:1.21.4+build.8:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.14")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.119.3+1.21.4")
    modApi("me.shedaniel.cloth:cloth-config-fabric:17.0.144")
    modApi("com.terraformersmc:modmenu:13.0.3")}

repositories {
    maven("https://maven.fabricmc.net/")
    maven("https://maven.terraformersmc.com/releases/")
    maven("https://maven.shedaniel.me/")
    mavenCentral()
}


java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
