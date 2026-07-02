plugins {
    id("multiloader-loader")
    alias(libs.plugins.neoforge.moddev)
}

val neoforgeVersion = libs.versions.ext.neoforge.api.get()
val parchmentMinecraft = libs.versions.ext.parchment.minecraft.get()
val parchmentVersion = libs.versions.ext.parchment.mappings.get()
val modId = providers.gradleProperty("mod_id").get()

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
