pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net")
		maven("https://maven.neoforged.net/releases")
		maven("https://maven.architectury.dev")
		maven("https://maven.kikugie.dev/snapshots")
		maven("https://maven.kikugie.dev/releases")
		maven("https://repo.polyfrost.cc/releases")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.6.1"
}

stonecutter {
	kotlinController = true
	centralScript = "build.gradle.kts"

	create(rootProject) {
		fun mc(mcVersion: String, loaders: Iterable<String>) {
			for (loader in loaders) {
				vers("$mcVersion-$loader", mcVersion)
			}
		}

		mc("1.20.4", listOf("fabric"))
		mc("1.21.1", listOf("fabric"))
		mc("1.21.5", listOf("neoforge"))
		mc("1.21.6", listOf("fabric", "neoforge"))

		vcsVersion = "1.21.6-fabric"
	}
}

rootProject.name = "Confirm Disconnect"
