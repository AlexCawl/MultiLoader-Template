plugins {
    id("multiloader-loader")
    alias(libs.plugins.fabric.loom)
}

val modId = providers.gradleProperty("mod_id").get()

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.layered {
        officialMojangMappings()
        parchment(libs.parchment.get())
    })
    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
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
