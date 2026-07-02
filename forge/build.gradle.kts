import net.minecraftforge.gradle.userdev.DependencyManagementExtension
import net.minecraftforge.gradle.userdev.UserDevExtension
import org.gradle.api.publish.maven.MavenPublication
import org.spongepowered.asm.gradle.plugins.MixinExtension

plugins {
    id("multiloader-loader")
    id("dev.alexcawl.multiloader.consumer")
    alias(libs.plugins.forge.gradle)
    alias(libs.plugins.mixin)
}

val modName = providers.gradleProperty("mod_name").get()
val modId = providers.gradleProperty("mod_id").get()
val minecraftVersion = libs.versions.ext.minecraft.current.get()
val forgeVersion = libs.versions.ext.forge.api.get()

base {
    archivesName = "$modName-forge-$minecraftVersion"
}

configure<MixinExtension> {
    config("$modId.mixins.json")
    config("$modId.forge.mixins.json")
}

tasks.jar {
    manifest {
        attributes["MixinConfigs"] = "$modId.mixins.json,$modId.forge.mixins.json"
    }
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
        named<MavenPublication>("mavenJava") {
            extensions.getByType<DependencyManagementExtension>().component(this)
        }
    }
}

sourceSets.configureEach {
    val dir = layout.buildDirectory.dir("sourcesSets/$name")
    output.setResourcesDir(dir)
    java.destinationDirectory = dir
}
