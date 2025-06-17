plugins {
	id("dev.architectury.loom") version "1.10.+"
	id("me.modmuss50.mod-publish-plugin") version "0.8.4"
	id("net.kyori.blossom") version "1.3.2"
}

class ModData {
	val id = property("mod.id").toString()
	val name = property("mod.name")
	val version = property("mod.version")
	val group = property("mod.group").toString()
	val description = property("mod.description")
	val source = property("mod.source")
	val issues = property("mod.issues")
	val license = property("mod.license").toString()
	val modrinth = property("mod.modrinth")
	val curseforge = property("mod.curseforge")
	val kofi = property("mod.kofi")
	val discord = property("mod.discord")
}

class Dependencies {
	val fabricApiVersion = property("deps.fabric_api_version")
	val modmenuVersion = property("deps.modmenu_version")
	val yaclVersion = property("deps.yacl_version")
	val devauthVersion = property("deps.devauth_version")
	val mixinconstraintsVersion = property("deps.mixinconstraints_version")
	val mixinsquaredVersion = property("deps.mixinsquared_version")
}

class LoaderData {
	val loader = loom.platform.get().name.lowercase()
	val isFabric = loader == "fabric"
	val isNeoforge = loader == "neoforge"
}

class McData {
	val version = property("mod.mc_version")
	val dep = property("mod.mc_dep").toString()
}

val mc = McData()
val mod = ModData()
val deps = Dependencies()
val loader = LoaderData()

version = "${mod.version}+${mc.version}-${loader.loader}"
group = mod.group
base { archivesName.set(mod.id) }

stonecutter.const("fabric", loader.isFabric)
stonecutter.const("neoforge", loader.isNeoforge)

stonecutter.allowExtensions("json")

blossom {
	replaceToken("@MODID@", mod.id)
}

loom {
	silentMojangMappingsLicense()

	runConfigs.all {
		ideConfigGenerated(stonecutter.current.isActive)
		runDir = "../../run" // This sets the run folder for all mc versions to the same folder. Remove this line if you want individual run folders.
	}

	runConfigs.remove(runConfigs["server"]) // Removes server run configs
}

loom.runs {
	afterEvaluate {
		val mixinJarFile = configurations.runtimeClasspath.get().incoming.artifactView {
			componentFilter {
				it is ModuleComponentIdentifier && it.group == "net.fabricmc" && it.module == "sponge-mixin"
			}
		}.files.first()

		configureEach {
			vmArg("-javaagent:$mixinJarFile") // Mixin Hotswap doesn't work on NeoForge, but doesn't hurt to keep
			vmArg("-XX:+AllowEnhancedClassRedefinition")

			property("mixin.hotSwap", "true")
			property("mixin.debug.export", "true") // Puts mixin outputs in run/.mixin.out
		}
	}
}

repositories {
	maven("https://maven.parchmentmc.org") // Parchment
	maven("https://maven.isxander.dev/releases") // YACL
	maven("https://thedarkcolour.github.io/KotlinForForge") // Kotlin for Forge - required by YACL
	maven("https://maven.terraformersmc.com") // Mod Menu
	maven("https://maven.nucleoid.xyz/") // Placeholder API - required by Mod Menu
	maven("https://maven.neoforged.net/releases") // NeoForge
	maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1") // DevAuth
	maven("https://maven.bawnorton.com/releases") // MixinSquared
	maven("https://api.modrinth.com/maven") // Modrinth
}

dependencies {
	minecraft("com.mojang:minecraft:${mc.version}")

	@Suppress("UnstableApiUsage")
	mappings(loom.layered {
		// Mojmap mappings
		officialMojangMappings()

		// Parchment mappings (it adds parameter mappings & javadoc)
		optionalProp("deps.parchment_version") {
			parchment("org.parchmentmc.data:parchment-${property("mod.mc_version")}:$it@zip")
		}
	})

	modRuntimeOnly("me.djtheredstoner:DevAuth-${loader.loader}:${deps.devauthVersion}")
	include(implementation("com.moulberry:mixinconstraints:${deps.mixinconstraintsVersion}")!!)!!
	include(implementation(annotationProcessor("com.github.bawnorton.mixinsquared:mixinsquared-${loader.loader}:${deps.mixinsquaredVersion}")!!)!!)

	if (loader.isFabric) {
		modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
		modImplementation("net.fabricmc.fabric-api:fabric-api:${deps.fabricApiVersion}+${mc.version}")
		modImplementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+${mc.version}-${loader.loader}")
		modImplementation("com.terraformersmc:modmenu:${deps.modmenuVersion}")
	} else if (loader.isNeoforge) {
		"neoForge"("net.neoforged:neoforge:${findProperty("deps.neoforge")}")
		implementation("dev.isxander:yet-another-config-lib:${deps.yaclVersion}+${mc.version}-${loader.loader}") { isTransitive = false }
	}

}

// mc_dep fields must be in the format 'x', '>=x', '>=x <=y'
val rangeRegex = Regex(""">=\s*([0-9.]+)(?:\s*<=\s*([0-9.]+))?""")
val exactVersionRegex = Regex("""^\d+\.\d+(\.\d+)?$""")

val modrinthId = findProperty("publish.modrinth")?.toString()?.takeIf { it.isNotBlank() }
val curseforgeId = findProperty("publish.curseforge")?.toString()?.takeIf { it.isNotBlank() }

// accessTokens should be placed in the user Gradle gradle.properties file
// for example, on Windows this would be "C:\Users\{user}\.gradle\gradle.properties"
// then add:
// modrinth.token=
// curseforge.token=
publishMods {
	file = project.tasks.remapJar.get().archiveFile

	displayName = "${mod.name} ${mod.version}"
	this.version = mod.version.toString()
	changelog = rootProject.file("CHANGELOG.md").readText()
	type = STABLE

	modLoaders.add(loader.loader)

	dryRun = modrinthId == null && curseforgeId == null

	if (modrinthId != null) {
		modrinth {
			projectId = property("publish.modrinth").toString()
			accessToken = findProperty("modrinth.token").toString()

			if (rangeRegex.matches(mc.dep)) {
				val match = rangeRegex.find(mc.dep)!!
				val minVersion = match.groupValues[1]
				val maxVersion = match.groupValues.getOrNull(2)?.takeIf { it.isNotBlank() } ?: "latest"

				minecraftVersionRange {
					start = minVersion
					end = maxVersion
				}
			} else if (exactVersionRegex.matches(mc.dep)) {
				minecraftVersions.add(mc.dep)
			}

			if (loader.isFabric) {
				requires("fabric-api")
				requires("yacl")
				requires("modmenu")
			} else if (loader.isNeoforge) {
				requires("yacl")
			}
		}
	}

	if (curseforgeId != null) {
		curseforge {
			projectId = property("publish.curseforge").toString()
			accessToken = findProperty("curseforge.token").toString()

			if (rangeRegex.matches(mc.dep)) {
				val match = rangeRegex.find(mc.dep)!!
				val minVersion = match.groupValues[1]
				val maxVersion = match.groupValues.getOrNull(2)?.takeIf { it.isNotBlank() } ?: "latest"

				minecraftVersionRange {
					start = minVersion
					end = maxVersion
				}
			} else if (exactVersionRegex.matches(mc.dep)) {
				minecraftVersions.add(mc.dep)
			}

			if (loader.isFabric) {
				requires("fabric-api")
				requires("yacl")
				optional("modmenu")
			} else if (loader.isNeoforge) {
				requires("yacl")
			}
		}
	}
}

java {
	// withSourcesJar() // Uncomment if you want sources
	val java = if (stonecutter.compare(
			stonecutter.current.version,
			"1.20.6"
		) >= 0
	) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
	sourceCompatibility = java
	targetCompatibility = java
}

tasks.processResources {
	val props = buildMap {
		put("id", mod.id)
		put("name", mod.name)
		put("version", mod.version)
		put("mcdep", mc.dep)
		put("description", mod.description)
		put("source", mod.source)
		put("issues", mod.issues)
		put("license", mod.license)
		put("modrinth", mod.modrinth)
		put("curseforge", mod.curseforge)
		put("kofi", mod.kofi)
		put("discord", mod.discord)
		put("modmenu_version", deps.modmenuVersion)
		put("yacl_version", deps.yaclVersion)

		if (loader.isNeoforge) {
			put("forgeConstraint", findProperty("modstoml.forge_constraint"))
		}
		if (mc.version == "1.20.1" || mc.version == "1.20.4") {
			put("forge_id", loader.loader)
		}
	}

	props.forEach(inputs::property)

	filesMatching("**/lang/en_us.json") { // Defaults description to English translation
		expand(props)
		filteringCharset = "UTF-8"
	}

	if (loader.isFabric) {
		filesMatching("fabric.mod.json") { expand(props) }
		exclude(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml"))
	}

	if (loader.isNeoforge) {
		if (mc.version == "1.20.4") {
			filesMatching("META-INF/mods.toml") { expand(props) }
			exclude("fabric.mod.json", "META-INF/neoforge.mods.toml")
		} else {
			filesMatching("META-INF/neoforge.mods.toml") { expand(props) }
			exclude("fabric.mod.json", "META-INF/mods.toml")
		}
	}
}

if (stonecutter.current.isActive) {
	rootProject.tasks.register("buildActive") {
		group = "project"
		dependsOn(tasks.named("build"))
	}
}

fun <T> optionalProp(property: String, block: (String) -> T?): T? =
	findProperty(property)?.toString()?.takeUnless { it.isBlank() }?.let(block)
