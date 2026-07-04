import org.gradle.api.publish.maven.MavenPublication

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.neoforge.moddev)
    id("dev.alexcawl.convention.repositories")
    id("dev.alexcawl.metadata")
    id("dev.alexcawl.multiloader.consumer")
}

dependencies {
    merged(project(":common"))
}

java {
    toolchain.languageVersion.set(libs.versions.ext.java.map { JavaLanguageVersion.of(it.toInt()) })
}

val neoforgeVersion = libs.versions.ext.neoforge.api.get()
val minecraftVersion = libs.versions.ext.minecraft.current.get()
val minecraftVersionRange = libs.versions.ext.minecraft.range.get()
val parchmentMinecraft = libs.versions.ext.parchment.minecraft.get()
val parchmentVersion = libs.versions.ext.parchment.mappings.get()
val modName = properties["mod_name"] as String
val modAuthor = properties["mod_author"] as String
val modId = properties["mod_id"] as String
val modLicense = properties["license"] as String
val credits = properties["credits"] as String
val description = properties["description"] as String

base {
    archivesName = "$modId-${project.name}-$minecraftVersion"
}

metadata {
    resources("META-INF/neoforge.mods.toml") {
        "version"(version)
        "minecraft_version_range"(minecraftVersionRange)
        "neoforge_version"(neoforgeVersion)
        "neoforge_loader_version_range"(libs.versions.ext.neoforge.loader.range.get())
        "mod_name"(modName)
        "mod_author"(modAuthor)
        "mod_id"(modId)
        "license"(modLicense)
        "description"(description)
        "credits"(credits)
    }
    resources("pack.mcmeta", "*.mixins.json") {
        "mod_name"(modName)
        "mod_id"(modId)
    }
    jarManifest {
        "Specification-Title"(modName)
        "Specification-Vendor"(modAuthor)
        "Specification-Version"(version)
        "Implementation-Title"(project.name)
        "Implementation-Version"(version)
        "Implementation-Vendor"(modAuthor)
        "Built-On-Minecraft"(minecraftVersion)
    }
}

neoForge {
    version = neoforgeVersion
    // Automatically enable neoforge AccessTransformers if the file exists
    val at = project(":common").file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    parchment {
        minecraftVersion = parchmentMinecraft
        mappingsVersion = parchmentVersion
    }
    runs {
        configureEach {
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            ideName = "NeoForge ${name.replaceFirstChar(Char::uppercase)} (${project.path})"
        }
        create("client") {
            client()
        }
        create("data") {
            data()
            programArguments.addAll(
                "--mod", modId,
                "--all",
                "--output", file("src/generated/resources/").absolutePath,
                "--existing", file("src/main/resources/").absolutePath
            )
        }
        create("server") {
            server()
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

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
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
