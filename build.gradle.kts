@file:Suppress("UnstableApiUsage", "PropertyName")

import dev.deftu.gradle.utils.GameSide

plugins {
    java
    kotlin("jvm")
    id("dev.deftu.gradle.multiversion") // Applies preprocessing for multiple versions of Minecraft and/or multiple mod loaders.
    id("dev.deftu.gradle.tools") // Applies several configurations to things such as the Java version, project name/version, etc.
    id("dev.deftu.gradle.tools.resources") // Applies resource processing so that we can replace tokens, such as our mod name/version, in our resources.
    id("dev.deftu.gradle.tools.bloom") // Applies the Bloom plugin, which allows us to replace tokens in our source files, such as being able to use `@MOD_VERSION` in our source files.
    id("dev.deftu.gradle.tools.shadow") // Applies the Shadow plugin, which allows us to shade our dependencies into our mod JAR. This is NOT recommended for Fabric mods, but we have an *additional* configuration for those!
    id("dev.deftu.gradle.tools.minecraft.loom") // Applies the Loom plugin, which automagically configures Essential's Architectury Loom plugin for you.
    id("dev.deftu.gradle.tools.minecraft.releases") // Applies the Minecraft auto-releasing plugin, which allows you to automatically release your mod to CurseForge and Modrinth.
}

toolkitLoomHelper {
    useOneConfig {
        version = "1.0.0-alpha.158"
        loaderVersion = "1.1.0-alpha.49"

        usePolyMixin = true
        polyMixinVersion = "0.8.4+build.7"

        applyLoaderTweaker = true

        for (module in arrayOf("commands", "config", "config-impl", "events", "internal", "ui", "utils")) {
            +module
        }
    }

    useDevAuth("1.2.1")
    useMixinExtras("0.4.1")

    disableRunConfigs(GameSide.SERVER)

    if (mcData.isForge) {
        useForgeMixin(modData.id)
    }
}

version = properties["mod.version"]!!
group = properties["mod.group"]!!

tasks.withType<ProcessResources>() {
    val expandMap = mapOf(
        "mod_id" to properties["mod.id"],
        "version" to version,
        "name" to properties["mod.name"],
        "description" to properties["mod.description"],
        "modrinth" to properties["mod.modrinth"],
        "source" to properties["mod.source"],
        "issues" to properties["mod.issues"],
        "license" to properties["mod.license"],
        "mc_dep" to "1.8.9",
        "kofi" to properties["mod.kofi"],
        "discord" to properties["mod.discord"],
        "modrinth" to properties["mod.modrinth"],
        "curseforge" to properties["mod.curseforge"],
    )

    inputs.properties(expandMap)

    filesMatching("fabric.mod.json") {
        expand(expandMap)
    }
}

dependencies {
    // Add (Legacy) Fabric API as dependencies (these are both optional but are particularly useful).
    if (mcData.isLegacyFabric) {
        modImplementation("net.legacyfabric.legacy-fabric-api:legacy-fabric-api:${mcData.dependencies.legacyFabric.legacyFabricApiVersion}")
    }
}
