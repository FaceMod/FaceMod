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
    minecraft("com.mojang:minecraft:1.21.3")
    mappings("net.fabricmc:yarn:1.21.3+build.2:v2")
    modImplementation("net.fabricmc:fabric-loader:0.16.10")
    modImplementation("net.fabricmc.fabric-api:fabric-api:0.114.0+1.21.3")
    modImplementation("net.fabricmc:access-widener:2.1.0")
}

java {
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

loom {
    accessWidenerPath = file("src/main/resources/facemod.accesswidener")
}