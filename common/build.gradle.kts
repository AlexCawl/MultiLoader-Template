plugins {
    `java-library`
    alias(libs.plugins.neoforge.moddev)
    id("dev.alexcawl.convention.repositories")
    id("dev.alexcawl.metadata")
    id("dev.alexcawl.multiloader.publisher")
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
val parchmentMinecraft = libs.versions.ext.parchment.minecraft.get()
val parchmentVersion = libs.versions.ext.parchment.mappings.get()

base {
    archivesName = "$modId-${project.name}-$minecraftVersion"
}

metadata {
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
    neoFormVersion = configuredNeoFormVersion
    // Automatically enable AccessTransformers if the file exists
    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
    parchment {
        minecraftVersion = parchmentMinecraft
        mappingsVersion = parchmentVersion
    }
}
