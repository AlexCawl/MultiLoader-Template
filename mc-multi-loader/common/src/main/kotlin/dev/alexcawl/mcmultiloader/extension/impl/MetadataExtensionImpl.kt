package dev.alexcawl.mcmultiloader.extension.impl

import dev.alexcawl.mcmultiloader.extension.MetadataExtension
import dev.alexcawl.mcmultiloader.extension.MetadataExtension.BuilderScope
import dev.alexcawl.mcmultiloader.extension.MetadataExtension.ResourceScope
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.domainObjectSet
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

abstract class MetadataExtensionImpl @Inject constructor(
    private val objects: ObjectFactory,
) : MetadataExtension {

    internal val resourcesExpands: DomainObjectSet<ResourceExpand> = objects.domainObjectSet(ResourceExpand::class)

    internal val jarManifestAttributes: MapProperty<String, String> = objects.mapProperty(String::class, String::class)

    override fun resources(action: Action<in ResourceScope>) {
        val resourceScope = objects.newInstance(ResourceScopeImpl::class, resourcesExpands)
        action.execute(resourceScope)
    }

    override fun jarManifest(action: Action<in BuilderScope>) {
        val builderScope = objects.newInstance(BuilderScopeImpl::class, jarManifestAttributes)
        action.execute(builderScope)
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
