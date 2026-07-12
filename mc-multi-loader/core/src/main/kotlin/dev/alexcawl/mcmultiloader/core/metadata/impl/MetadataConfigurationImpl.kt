package dev.alexcawl.mcmultiloader.core.metadata.impl

import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration.BuilderScope
import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration.ResourceScope
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.domainObjectSet
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.withType
import javax.inject.Inject

internal abstract class MetadataConfigurationImpl @Inject constructor(
    private val project: Project,
    private val objects: ObjectFactory,
) : MetadataConfiguration {

    override fun resources(action: Action<in ResourceScope>) {
        val resourcesExpands: DomainObjectSet<ResourceExpand> = objects.domainObjectSet(ResourceExpand::class)
        val resourceScope = objects.newInstance(ResourceScopeImpl::class, resourcesExpands)
        action.execute(resourceScope)
        with(project) {
            plugins.withType<JavaPlugin> {
                configureResources(resourcesExpands)
            }
        }
    }

    override fun jarManifest(action: Action<in BuilderScope>) {
        val jarManifestAttributes: MapProperty<String, String> = objects.mapProperty(String::class, String::class)
        val builderScope = objects.newInstance(BuilderScopeImpl::class, jarManifestAttributes)
        action.execute(builderScope)
        with(project) {
            plugins.withType<JavaPlugin> {
                configureJarManifest(jarManifestAttributes)
            }
        }
    }
}

private abstract class ResourceScopeImpl @Inject constructor(
    private val objects: ObjectFactory,
    private val resourcesExpands: DomainObjectSet<ResourceExpand>,
) : ResourceScope {

    override fun resource(vararg patterns: String, action: Action<in BuilderScope>) {
        val resourceExpand = objects.newInstance(ResourceExpand::class)
        val builder = objects.newInstance(BuilderScopeImpl::class, resourceExpand.attributes)
        resourceExpand.patterns.addAll(patterns.toList())
        action.execute(builder)
        resourcesExpands.add(resourceExpand)
    }
}

private abstract class BuilderScopeImpl @Inject constructor(
    private val values: MapProperty<String, String>,
) : BuilderScope {

    override fun String.invoke(value: String) {
        values.put(this, value)
    }

    override fun String.invoke(value: Provider<String>) {
        values.put(this, value)
    }
}
