import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.artifacts.VersionCatalogsExtension

plugins {
    `java-library`
    `maven-publish`
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
val javaVersion = libs.findVersion("ext-java").get().requiredVersion
val minecraftVersion = libs.findVersion("ext-minecraft-current").get().requiredVersion
val minecraftVersionRange = libs.findVersion("ext-minecraft-range").get().requiredVersion
val modName = providers.gradleProperty("mod_name").get()
val modAuthor = providers.gradleProperty("mod_author").get()
val modId = providers.gradleProperty("mod_id").get()
val modLicense = providers.gradleProperty("license").get()
val credits = providers.gradleProperty("credits").get()
val fabricVersion = libs.findVersion("ext-fabric-api").get().requiredVersion
val fabricLoaderVersion = libs.findVersion("ext-fabric-loader").get().requiredVersion
val neoforgeVersion = libs.findVersion("ext-neoforge-api").get().requiredVersion
val neoforgeLoaderVersionRange = libs.findVersion("ext-neoforge-loader-range").get().requiredVersion
val forgeVersion = libs.findVersion("ext-forge-api").get().requiredVersion
val forgeLoaderVersionRange = libs.findVersion("ext-forge-loader-range").get().requiredVersion

base {
    archivesName = "$modId-${project.name}-$minecraftVersion"
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion.toInt())
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
    exclusiveContent {
        forRepository {
            maven {
                name = "Minecraft Libraries"
                url = uri("https://libraries.minecraft.net")
            }
        }
        filter { includeGroup("org.lwjgl") }
    }
    // https://docs.gradle.org/current/userguide/declaring_repositories.html#declaring_content_exclusively_found_in_one_repository
    exclusiveContent {
        forRepository {
            maven {
                name = "Sponge"
                url = uri("https://repo.spongepowered.org/repository/maven-public")
            }
        }
        filter { includeGroupAndSubgroups("org.spongepowered") }
    }
    exclusiveContent {
        forRepositories(
            maven {
                name = "ParchmentMC"
                url = uri("https://maven.parchmentmc.org/")
            },
            maven {
                name = "NeoForge"
                url = uri("https://maven.neoforged.net/releases")
            }
        )
        filter { includeGroup("org.parchmentmc.data") }
    }
    maven {
        name = "BlameJared"
        url = uri("https://maven.blamejared.com")
    }
}

// Declare capabilities on the outgoing configurations.
// Read more about capabilities here: https://docs.gradle.org/current/userguide/component_capabilities.html#sec:declaring-additional-capabilities-for-a-local-component
listOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements").forEach { variant ->
    configurations.named(variant) {
        outgoing {
            capability("$group:${project.name}:$version")
            capability("$group:${base.archivesName.get()}:$version")
            capability("$group:$modId-${project.name}-$minecraftVersion:$version")
            capability("$group:$modId:$version")
        }
    }
    publishing.publications.withType<MavenPublication>().configureEach {
        suppressPomMetadataWarningsFor(variant)
    }
}

tasks.named<Jar>("sourcesJar") {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_$modName" }
    }
}

tasks.jar {
    from(rootProject.file("LICENSE")) {
        rename { "${it}_$modName" }
    }

    manifest {
        attributes(
            mapOf(
                "Specification-Title" to modName,
                "Specification-Vendor" to modAuthor,
                "Specification-Version" to archiveVersion.get(),
                "Implementation-Title" to project.name,
                "Implementation-Version" to archiveVersion.get(),
                "Implementation-Vendor" to modAuthor,
                "Built-On-Minecraft" to minecraftVersion
            )
        )
    }
}

tasks.processResources {
    val expandProps = mapOf(
        "version" to version,
        "group" to project.group,
        "minecraft_version" to minecraftVersion,
        "minecraft_version_range" to minecraftVersionRange,
        "fabric_version" to fabricVersion,
        "fabric_loader_version" to fabricLoaderVersion,
        "mod_name" to modName,
        "mod_author" to modAuthor,
        "mod_id" to modId,
        "license" to modLicense,
        "description" to (project.description ?: ""),
        "neoforge_version" to neoforgeVersion,
        "neoforge_loader_version_range" to neoforgeLoaderVersionRange,
        "forge_version" to forgeVersion,
        "forge_loader_version_range" to forgeLoaderVersionRange,
        "credits" to credits,
        "java_version" to javaVersion
    )

    filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/mods.toml", "META-INF/neoforge.mods.toml", "*.mixins.json")) {
        expand(expandProps)
    }
    inputs.properties(expandProps)
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
        }
    }
    repositories {
        maven {
            System.getenv("local_maven_url")?.let { url = uri(it) }
        }
    }
}
