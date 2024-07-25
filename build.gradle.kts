plugins {
    `java-library`
    id("fabric-loom") version "1.6-SNAPSHOT"
}

tasks.withType<net.fabricmc.loom.task.AbstractRemapJarTask> {
    archiveVersion = ""
}

group = "main.java.io.github.facemod"
version = "1.0.0-SNAPSHOT"

dependencies {
    minecraft("com.mojang:minecraft:1.20.4")
    mappings("net.fabricmc:yarn:1.20.4+build.3:v2")
    modImplementation("net.fabricmc:fabric-loader:0.15.10")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.96.11+1.20.4")
    modImplementation("net.fabricmc:access-widener:2.1.0")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

loom {
    accessWidenerPath = file("src/main/resources/facemod.accesswidener")
}