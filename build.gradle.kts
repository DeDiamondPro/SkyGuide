import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import gg.essential.gradle.util.noServerRunConfigs

plugins {
    kotlin("jvm")
    kotlin("plugin.serialization") version "1.4.21"
    id("gg.essential.multi-version")
    id("gg.essential.defaults.repo")
    id("gg.essential.defaults.java")
    id("gg.essential.defaults.loom")
    id("com.github.johnrengelman.shadow")
    id("net.kyori.blossom") version "1.3.0"
    id("io.github.juuxel.loom-quiltflower-mini")
    id("signing")
    java
}

val mod_name: String by project
val mod_version: String by project
val mod_id: String by project

preprocess {
    vars.put("MODERN", if (project.platform.mcMinor >= 16) 1 else 0)
}

blossom {
    replaceToken("@VER@", mod_version)
    replaceToken("@NAME@", mod_name)
    replaceToken("@ID@", mod_id)
}

version = mod_version
group = "dev.dediamondpro"
base {
    archivesName.set("$mod_name ($platform)")
}
loom {
    noServerRunConfigs()
    if (project.platform.isForge) {
        launchConfigs.named("client") {
            arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
            property("mixin.debug.export", "true")
        }
        forge {
            mixinConfig("mixins.${mod_id}.json")
        }
    }
    mixin.defaultRefmapName.set("mixins.${mod_id}.refmap.json")
}

val shade: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

sourceSets {
    main {
        output.setResourcesDir(java.classesDirectory)
    }
}

repositories {
    maven("https://repo.polyfrost.cc/releases")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
    modRuntimeOnly("me.djtheredstoner:DevAuth-forge-legacy:1.1.0")

    shade("gg.essential:loader-launchwrapper:1.1.3")
    modCompileOnly("gg.essential:essential-1.8.9-forge:3760")

    shade("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:1.3.2") {
        exclude(module = "*")
    }
    shade("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.3.2") {
        exclude(module = "*")
    }

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
    compileOnly("org.spongepowered:mixin:0.8.5")
}

tasks.processResources {
    inputs.property("id", mod_id)
    inputs.property("name", mod_name)
    val java = if (project.platform.mcMinor >= 18) {
        17
    } else {
        if (project.platform.mcMinor == 17) 16 else 8
    }
    val compatLevel = "JAVA_${java}"
    inputs.property("java", java)
    inputs.property("java_level", compatLevel)
    inputs.property("version", mod_version)
    inputs.property("mcVersionStr", project.platform.mcVersionStr)
    filesMatching(listOf("mcmod.info", "mixins.${mod_id}.json", "mods.toml")) {
        expand(
            mapOf(
                "id" to mod_id,
                "name" to mod_name,
                "java" to java,
                "java_level" to compatLevel,
                "version" to mod_version,
                "mcVersionStr" to project.platform.mcVersionStr
            )
        )
    }
    filesMatching("fabric.mod.json") {
        expand(
            mapOf(
                "id" to mod_id,
                "name" to mod_name,
                "java" to java,
                "java_level" to compatLevel,
                "version" to mod_version,
                "mcVersionStr" to project.platform.mcVersionStr.substringBeforeLast(".") + ".x"
            )
        )
    }
}

tasks {
    withType(Jar::class.java) {
        if (project.platform.isFabric) {
            exclude("mcmod.info", "mods.toml")
        } else {
            exclude("fabric.mod.json")
            if (project.platform.isLegacyForge) {
                exclude("mods.toml")
            } else {
                exclude("mcmod.info")
            }
        }
    }
    named<ShadowJar>("shadowJar") {
        archiveClassifier.set("dev")
        configurations = listOf(shade)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
    remapJar {
        input.set(shadowJar.get().archiveFile)
        archiveClassifier.set("")
    }
    jar {
        manifest {
            attributes(mapOf(
                "ModSide" to "CLIENT",
                "TweakOrder" to "0",
                "TweakClass" to "gg.essential.loader.stage0.EssentialSetupTweaker",
                "ForceLoadAsMod" to true
            ))
        }
        dependsOn(shadowJar)
        archiveClassifier.set("")
        enabled = false
    }
}