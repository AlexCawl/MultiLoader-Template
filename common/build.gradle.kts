plugins {
    id("multiloader-common")
    id("dev.alexcawl.multiloader.publisher")
    alias(libs.plugins.neoforge.moddev)
}

val configuredNeoFormVersion = libs.versions.ext.neo.form.get()
val parchmentMinecraft = libs.versions.ext.parchment.minecraft.get()
val parchmentVersion = libs.versions.ext.parchment.mappings.get()

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

dependencies {
    compileOnly(libs.mixin.core)
    // fabric and neoforge both bundle mixinextras, so it is safe to use it in common
    compileOnly(libs.mixinextras)
    annotationProcessor(libs.mixinextras)
}
