package dev.alexcawl.multiloader

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.attributes.Usage
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named
import org.gradle.language.jvm.tasks.ProcessResources

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
        }
    }

    private fun Project.merged(): Provider<out Configuration> {
        return configurations.dependencyScope("merged")
    }

    private fun Project.mergedUsage(): Usage {
        return objects.named<Usage>(MERGED_USAGE)
    }

    private fun Project.mergedJava(merged: Provider<out Configuration>): NamedDomainObjectProvider<ResolvableConfiguration> {
        return configurations.resolvable("mergedJava") {
            extendsFrom(merged.get())
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, mergedUsage())
                attribute(MergedSource.ATTRIBUTE, MergedSource.JAVA)
            }
        }
    }

    private fun Project.mergedResources(merged: Provider<out Configuration>): NamedDomainObjectProvider<ResolvableConfiguration> {
        return configurations.resolvable("mergedResources") {
            extendsFrom(merged.get())
            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, mergedUsage())
                attribute(MergedSource.ATTRIBUTE, MergedSource.RESOURCE)
            }
        }
    }

    private fun Project.extendCompileOnly(merged: Provider<out Configuration>) {
        configurations.named(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME) {
            extendsFrom(merged.get())
        }
    }
}
