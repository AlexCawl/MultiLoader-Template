package dev.alexcawl.multiloader

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.attributes.Usage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources

private const val ACCESS_TRANSFORMER_PATH = "src/main/resources/META-INF/accesstransformer.cfg"
private const val ACCESS_WIDENER_PATH = "src/main/resources/accesswidener"

class ConsumerModulePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val merged = target.merged()
        target.withJavaPlugin {
            java {
                withJavadocJar()
                withSourcesJar()
            }
            val mergedJava = target.mergedJava(merged)
            val mergedResources = target.mergedResources(merged)
            target.extendCompileOnly(merged)
            val mainSourceSet = target.mainSourceSet()
            mainSourceSet {
                target.tasks.named<JavaCompile>(compileJavaTaskName) {
                    source(mergedJava)
                }
                target.tasks.named<ProcessResources>(processResourcesTaskName) {
                    from(mergedResources)
                }
                target.tasks.named<Javadoc>(javadocTaskName) {
                    source(mergedJava)
                }
                target.tasks.named<Jar>(sourcesJarTaskName) {
                    from(mergedJava)
                    from(mergedResources)
                }
            }
            target.configureFabricAccessWidener(merged)
            target.configureNeoforgeAccessTransformers(merged)
            target.configureForgeAccessTransformer()
        }
    }

    private fun Project.configureFabricAccessWidener(merged: NamedDomainObjectProvider<out Configuration>) {
        withFabricPlugin {
            fabric {
                merged {
                    dependencies.withType(ProjectDependency::class.java) {
                        val aw = dependencyProject.file(ACCESS_WIDENER_PATH)
                        if (aw.exists()) {
                            accessWidenerPath.set(aw)
                        }
                    }
                }
            }
        }
    }

    // Automatically enable neoforge AccessTransformers if the file exists
    private fun Project.configureNeoforgeAccessTransformers(merged: NamedDomainObjectProvider<out Configuration>) {
        withNeoforgePlugin {
            neoforge {
                merged {
                    dependencies.withType(ProjectDependency::class.java) {
                        val at = dependencyProject.file(ACCESS_TRANSFORMER_PATH)
                        if (at.exists()) {
                            accessTransformers.from(at.absolutePath)
                        }
                    }
                }
            }
        }
    }

    // Automatically enable forge AccessTransformers if the file exists.
    // This location is hardcoded in Forge and can not be changed.
    // Forge still uses SRG names during compile time, so we cannot use the common AT's.
    private fun Project.configureForgeAccessTransformer() {
        withForgePlugin {
            forge {
                val at = file(ACCESS_TRANSFORMER_PATH)
                if (at.exists()) {
                    accessTransformer(at)
                }
            }
        }
    }

    private fun Project.merged(): NamedDomainObjectProvider<out Configuration> {
        return configurations.dependencyScope("merged")
    }

    private fun Project.mergedUsage(): Usage {
        return objects.named<Usage>(MERGED_USAGE)
    }

    private fun Project.mergedJava(merged: NamedDomainObjectProvider<out Configuration>): NamedDomainObjectProvider<ResolvableConfiguration> {
        return configurations.resolvable("mergedJava") {
            extendsFrom(merged.get())
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, mergedUsage())
                attribute(MergedSource.ATTRIBUTE, MergedSource.JAVA)
            }
        }
    }

    private fun Project.mergedResources(merged: NamedDomainObjectProvider<out Configuration>): NamedDomainObjectProvider<ResolvableConfiguration> {
        return configurations.resolvable("mergedResources") {
            extendsFrom(merged.get())
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, mergedUsage())
                attribute(MergedSource.ATTRIBUTE, MergedSource.RESOURCE)
            }
        }
    }

    private fun Project.extendCompileOnly(merged: NamedDomainObjectProvider<out Configuration>) {
        configurations.named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME) {
            extendsFrom(merged.get())
        }
    }
}
