plugins {
    kotlin("plugin.serialization") version "1.7.10"
}

repositories {
    gradlePluginPortal()
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
}

dependencies {
    implementation("com.charleskorn.kaml:kaml:0.46.0")
    implementation("net.dv8tion:JDA:5.0.0-alpha.13") {
        exclude(module = "opus-java")
    }
    compileOnly("net.kyori:adventure-text-minimessage:4.11.0")
    compileOnly("com.velocitypowered:velocity-api:3.1.0")
}