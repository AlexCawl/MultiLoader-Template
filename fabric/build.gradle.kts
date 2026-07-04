import org.gradle.api.publish.maven.MavenPublication

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.fabric.loom)
    id("dev.alexcawl.convention.repositories")
    id("dev.alexcawl.metadata")
    id("dev.alexcawl.multiloader.consumer")
}

dependencies {
    merged(project(":common"))
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(libs.parchment.get())
    })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
}

java {
    toolchain.languageVersion.set(libs.versions.ext.java.map { JavaLanguageVersion.of(it.toInt()) })
}

val modName = properties["mod_name"] as String
val modAuthor = properties["mod_author"] as String
val modId = properties["mod_id"] as String
val modLicense = properties["license"] as String
val description = properties["description"] as String
val javaVersion = libs.versions.ext.java.get()
val minecraftVersion = libs.versions.ext.minecraft.current.get()

base {
    archivesName = "$modId-${project.name}-$minecraftVersion"
}

metadata {
    resources("pack.mcmeta", "fabric.mod.json", "*.mixins.json") {
        "version"(version)
        "minecraft_version"(minecraftVersion)
        "fabric_loader_version"(libs.versions.ext.fabric.loader.get())
        "mod_name"(modName)
        "mod_author"(modAuthor)
        "mod_id"(modId)
        "license"(modLicense)
        "description"(description)
        "java_version"(javaVersion)
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

loom {
    val aw = project(":common").file("src/main/resources/$modId.accesswidener")
    if (aw.exists()) {
        accessWidenerPath.set(aw)
    }
    mixin {
        defaultRefmapName.set("$modId.refmap.json")
    }
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("runs/client")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("runs/server")
        }
    }
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
