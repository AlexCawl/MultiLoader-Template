plugins {
    `java-library`
    alias(libs.plugins.fabric.loom)
    id("dev.alexcawl.convention.repositories")
    id("dev.alexcawl.mcmultiloader.fabric")
}

dependencies {
    mmlImplementation(project(":common"))
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
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

mcFabricLoader {
    metadata {
        resources {
            resource("fabric.mod.json") {
                "version"(version.toString())
                "minecraft_version"(minecraftVersion)
                "fabric_loader_version"(libs.versions.ext.fabric.loader)
                "mod_name"(modName)
                "mod_author"(modAuthor)
                "mod_id"(modId)
                "license"(modLicense)
                "description"(description)
                "java_version"(javaVersion)
            }
            resource("$modId.fabric.mixins.json") {}
        }

        jarManifest {
            "Specification-Title"(modName)
            "Specification-Vendor"(modAuthor)
            "Specification-Version"(version.toString())
            "Implementation-Title"(project.name)
            "Implementation-Version"(version.toString())
            "Implementation-Vendor"(modAuthor)
            "Built-On-Minecraft"(minecraftVersion)
        }
    }

    access {
        fabricAccessWidener("src/main/resources/META-INF/fabric.accesswidener")
    }
}

loom {
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
        create("datagen") {
            client()
            configName = "Fabric Datagen"
            ideConfigGenerated(true)
            runDir("runs/datagen")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${file("src/generated/resources").absolutePath}")
            vmArg("-Dfabric-api.datagen.modid=$modId")
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}
