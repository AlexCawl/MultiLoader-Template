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

mcMultiLoader {
    resourceTemplates {
        template("pack.mcmeta") {
            "mod_name"(modName)
        }
        mixinConfig("$modId.mixins.json") {
            "mod_id"(modId)
        }
    }

    fabricAccessWidener.set("accesswidener")
    neoForgeAccessTransformer.set("META-INF/accesstransformer.cfg")

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
    neoFormVersion = configuredNeoFormVersion
}
