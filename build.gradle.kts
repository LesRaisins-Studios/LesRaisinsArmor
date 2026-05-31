import java.time.Instant

plugins {
    `java-library`
    id("net.neoforged.moddev.legacyforge") version "2.0.141"
    id("eclipse")
    id("idea")
}

val mcVersion = property("minecraft_version").toString()
val mcVersionRange = property("minecraft_version_range").toString()
val forgeVersion = property("forge_version").toString()
val forgeVersionRange = property("forge_version_range").toString()
val loaderVersionRange = property("loader_version_range").toString()
val mapping_version = property("mapping_version").toString()
val jeiVersion = property("jei_version").toString()
val modId = property("mod_id").toString()
val modName = property("mod_name").toString()
val modLicense = property("mod_license").toString()
val modVersion = property("mod_version").toString()
val modGroupId = property("mod_group_id").toString()
val modAuthors = property("mod_authors").toString()
val modDescription = property("mod_description").toString()

group = modGroupId
version = modVersion

base {
    archivesName.set("$modId-$mcVersion")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

legacyForge {
    this.version = "$mcVersion-$forgeVersion"

    parchment {
        minecraftVersion.set(mcVersion)
        mappingsVersion.set(mapping_version)
    }

    runs {
        create("client") {
            client()
            gameDirectory = project.file("run")
        }
        create("server") {
            server()
            gameDirectory = project.file("run-server")
        }
        create("data") {
            data()
            gameDirectory = project.file("run-data")
            programArgument("--mod")
            programArgument(modId)
            programArgument("--output")
            programArgument(file("src/generated/resources/").absolutePath)
            programArgument("--existing")
            programArgument(file("src/main/resources/").absolutePath)
            programArgument("--client")
        }
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("forge.logging.console.level", "debug")
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

repositories {
    maven("https://www.cursemaven.com")

    maven("https://maven.kosmx.dev/") {
        name = "KosmX's maven"
    }

    maven("https://dvs1.progwml6.com/files/maven/") {
        name = "Progwml6's maven"
    }

    maven("https://modmaven.dev") {
        name = "ModMaven"
    }

    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.mcmodderanchor")
        }
    }
}

dependencies {
    modImplementation("curse.maven:timeless-and-classics-zero-1028108:5529117-sources-5529578")

    jarJar(modImplementation(group = "com.github.mcmodderanchor", name = "simplebedrockmodel", version = "2.3.3-forge-mc1.20.1")) {
        version {
            strictly("2.3.3-forge-mc1.20.1")
        }
    }

    compileOnly("com.maydaymemory:mae:1.1.2") {
        exclude(group = "com.google.code.findbugs", module = "jsr305")
        exclude(group = "it.unimi.dsi", module = "fastutil")
        exclude(group = "org.joml", module = "joml")
    }

    modCompileOnly("curse.maven:epic-fight-mod-405076:7316952")
    modCompileOnly("curse.maven:parcool-482378:6652972")

    compileOnly("mezz.jei:jei-$mcVersion-common-api:$jeiVersion")
    compileOnly("mezz.jei:jei-$mcVersion-forge-api:$jeiVersion")
//    runtimeOnly("mezz.jei:jei-$mcVersion-forge:$jeiVersion")

    modRuntimeOnly("dev.kosmx.player-anim:player-animation-lib-forge:1.0.2-rc1+1.20")

    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")
}

tasks.named<ProcessResources>("processResources") {
    val replaceProperties = mapOf(
        "minecraft_version" to mcVersion,
        "minecraft_version_range" to mcVersionRange,
        "forge_version" to forgeVersion,
        "forge_version_range" to forgeVersionRange,
        "loader_version_range" to loaderVersionRange,
        "mod_id" to modId,
        "mod_name" to modName,
        "mod_license" to modLicense,
        "mod_version" to modVersion,
        "mod_authors" to modAuthors,
        "mod_description" to modDescription,
        "jei_version" to jeiVersion,
    )

    inputs.properties(replaceProperties)

    filesMatching(listOf("META-INF/mods.toml", "pack.mcmeta")) {
        expand(replaceProperties)
    }
}

tasks.named<Jar>("jar") {
    manifest {
        attributes(
            mapOf(
                "Specification-Title" to modId,
                "Specification-Vendor" to modAuthors,
                "Specification-Version" to "1",
                "Implementation-Title" to project.name,
                "Implementation-Version" to version,
                "Implementation-Vendor" to modAuthors,
                "Implementation-Timestamp" to Instant.now().toString(),
                "MixinConfigs" to "$modId.mixins.json",
            )
        )
    }

    finalizedBy("reobfJar")
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
    config("$modId.mixins.json")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}
