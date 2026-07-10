plugins {
    `java-library`
    alias(libs.plugins.neoforge.moddev)
    id("dev.alexcawl.convention.repositories")
    id("dev.alexcawl.mcmultiloader.common")
}

dependencies {
    compileOnly(libs.mixin.core)
    // fabric and neoforge both bundle mixinextras, so it is safe to use it in common
    compileOnly(libs.mixinextras)
    annotationProcessor(libs.mixinextras)
}

java {
    toolchain.languageVersion.set(libs.versions.ext.java.map { JavaLanguageVersion.of(it.toInt()) })
}

val modName = properties["mod_name"] as String
val modAuthor = properties["mod_author"] as String
val modId = properties["mod_id"] as String
val minecraftVersion = libs.versions.ext.minecraft.current.get()
val configuredNeoFormVersion = libs.versions.ext.neo.form.get()

base {
    archivesName = "$modId-${project.name}-$minecraftVersion"
}

mcCommonLoader {
    access {
        fabricAccessWidener("src/main/resources/accesswidener")
        neoForgeAccessTransformer("src/main/resources/META-INF/accesstransformer.cfg")
    }

    metadata {
        resources {
            resource("pack.mcmeta") {
                "mod_name"(modName)
            }
            resource("$modId.mixins.json") {
                "mod_id"(modId)
            }
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
}

neoForge {
    neoFormVersion = configuredNeoFormVersion
}
