import net.minecraftforge.gradle.userdev.DependencyManagementExtension
import net.minecraftforge.gradle.userdev.UserDevExtension
import org.gradle.api.publish.maven.MavenPublication
import org.spongepowered.asm.gradle.plugins.MixinExtension

plugins {
    `java-library`
    `maven-publish`
    id("dev.alexcawl.convention.repositories")
    id("dev.alexcawl.metadata")
    id("dev.alexcawl.multiloader.consumer")
    alias(libs.plugins.forge.gradle)
    alias(libs.plugins.mixin)
}

java {
    toolchain.languageVersion.set(libs.versions.ext.java.map { JavaLanguageVersion.of(it.toInt()) })
}

val modName = properties["mod_name"] as String
val modAuthor = properties["mod_author"] as String
val modId = properties["mod_id"] as String
val modLicense = properties["license"] as String
val credits = properties["credits"] as String
val description = properties["description"] as String
val minecraftVersion = libs.versions.ext.minecraft.current.get()
val minecraftVersionRange = libs.versions.ext.minecraft.range.get()
val forgeVersion = libs.versions.ext.forge.api.get()

base {
    archivesName = "$modId-${project.name}-$minecraftVersion"
}

metadata {
    resources("META-INF/mods.toml") {
        "version"(version)
        "minecraft_version_range"(minecraftVersionRange)
        "forge_version"(forgeVersion)
        "forge_loader_version_range"(libs.versions.ext.forge.loader.range.get())
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
        "MixinConfigs"("$modId.mixins.json,$modId.forge.mixins.json")
    }
}

configure<MixinExtension> {
    config("$modId.mixins.json")
    config("$modId.forge.mixins.json")
}

configure<UserDevExtension> {
    mappings("official", minecraftVersion)

    copyIdeResources = true // Calls processResources when in dev
    reobf = false // Forge 1.20.6+ uses official mappings at runtime, so we shouldn't reobf from official to SRG

    // Automatically enable forge AccessTransformers if the file exists.
    // This location is hardcoded in Forge and can not be changed.
    // Forge still uses SRG names during compile time, so we cannot use the common AT's.
    val at = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformer(at)
    }

    runs {
        create("client") {
            workingDirectory(file("runs/client"))
            ideaModule = "${rootProject.name}.${project.name}.main"
            taskName = "Client"
            mods {
                create("modClientRun") {
                    source(sourceSets.main.get())
                }
            }
        }
        create("server") {
            workingDirectory(file("runs/server"))
            ideaModule = "${rootProject.name}.${project.name}.main"
            taskName = "Server"
            mods {
                create("modServerRun") {
                    source(sourceSets.main.get())
                }
            }
        }
        create("data") {
            workingDirectory(file("runs/data"))
            ideaModule = "${rootProject.name}.${project.name}.main"
            args("--mod", modId, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources/"))
            taskName = "Data"
            mods {
                create("modDataRun") {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

dependencies {
    merged(project(":common"))
    minecraft(libs.forge.minecraft) {
        version { require("$minecraftVersion-$forgeVersion") }
    }
    annotationProcessor(variantOf(libs.mixin.processor) {
        classifier("processor")
    })

    // Forge's hack fix
    implementation(libs.jopt.simple)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = base.archivesName.get()
            from(components["java"])
            extensions.getByType<DependencyManagementExtension>().component(this)
        }
    }
    repositories {
        maven {
            System.getenv("local_maven_url")?.let { url = uri(it) }
        }
    }
}

sourceSets.configureEach {
    val dir = layout.buildDirectory.dir("sourcesSets/$name")
    output.setResourcesDir(dir)
    java.destinationDirectory = dir
}
