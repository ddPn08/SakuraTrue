import com.github.jengelman.gradle.plugins.shadow.tasks.*

plugins {
    kotlin("jvm") version ("1.7.10")
    id("com.github.johnrengelman.shadow") version "7.1.1"
}

group = "run.dn5.sasa"
version = "1.0-beta.1"
description = "Plugin to authenticate users using Discord."
val artifactName = "${rootProject.name}-${rootProject.version}.jar"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(project(":paper"))
    implementation(project(":waterfall"))
    implementation(project(":velocity"))
}

tasks {
    shadowJar {
        dependencies {
            listOf(
                "net.dv8tion",
                "com.charleskorn.kaml",
                "kotlin",
            ).forEach {
                relocate(it, "run.dn5.sasa.sakuratrue.shadow.$it")
            }
        }

        minimize {
            exclude(project(":paper"))
            exclude(project(":waterfall"))
            exclude(project(":velocity"))
        }
        archiveFileName.set(artifactName)
    }

    register("preDebug") {
        dependsOn("clean", "shadowJar")
        doLast {
            listOf("paper", "waterfall", "velocity").forEach {
                copy {
                    from("$buildDir/libs/${artifactName}")
                    into(".debug/$it/plugins")
                }
            }
        }
    }
}

subprojects {
    group = parent!!.group
    version = parent!!.version
    description = parent!!.description

    apply {
        plugin("java")
        plugin("org.jetbrains.kotlin.jvm")
        plugin("com.github.johnrengelman.shadow")
    }

    repositories {
        mavenCentral()
    }

    dependencies {
        if (project.name != "common") implementation(project(":common"))
        compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.7.10")
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }


    tasks {
        processResources {
            filteringCharset = "UTF-8"
            filesMatching(listOf("bungee.yml", "plugin.yml", "velocity-plugin.json")) {
                expand(
                    mapOf(
                        "name" to rootProject.name,
                        "id" to rootProject.name.toLowerCase(),
                        "group" to project.group,
                        "version" to project.version,
                        "description" to project.description,
                        "author" to "ddPn08"
                    )
                )
            }
        }
        compileKotlin {
            kotlinOptions.jvmTarget = "17"
        }
    }
}