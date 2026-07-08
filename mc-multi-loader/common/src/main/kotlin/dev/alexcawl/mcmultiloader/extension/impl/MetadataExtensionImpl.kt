package dev.alexcawl.mcmultiloader.extension.impl

import dev.alexcawl.mcmultiloader.extension.MetadataExtension
import dev.alexcawl.mcmultiloader.extension.MetadataExtension.BuilderScope
import dev.alexcawl.mcmultiloader.extension.MetadataExtension.TemplateScope
import org.gradle.api.Action
import org.gradle.api.DomainObjectSet
import org.gradle.api.file.ProjectLayout
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

    internal val templates: DomainObjectSet<Template> = objects.domainObjectSet(Template::class)

    internal val jarManifestAttributes: MapProperty<String, String> = objects.mapProperty(String::class, String::class)

    override fun template(action: Action<in TemplateScope>) {
        val templateScope = objects.newInstance(TemplateScopeImpl::class, templates)
        action.execute(templateScope)
    }

    override fun jarManifest(action: Action<in BuilderScope>) {
        val builderScope = objects.newInstance(BuilderScopeImpl::class, jarManifestAttributes)
        action.execute(builderScope)
    }
}

private abstract class TemplateScopeImpl @Inject constructor(
    private val layout: ProjectLayout,
    private val objects: ObjectFactory,
    private val templates: DomainObjectSet<Template>,
) : TemplateScope {

    override fun template(vararg paths: String, action: Action<in BuilderScope>) {
        val template = objects.newInstance(Template::class)
        val builder = objects.newInstance(BuilderScopeImpl::class, template.attributes)
        template.files.from(layout.projectDirectory.files(paths))
        action.execute(builder)
        templates.add(template)
    }

    override fun template(path: Provider<String>, action: Action<in BuilderScope>) {
        val template = objects.newInstance(Template::class)
        val builder = objects.newInstance(BuilderScopeImpl::class, template.attributes)
        template.files.from(layout.projectDirectory.file(path))
        action.execute(builder)
        templates.add(template)
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
