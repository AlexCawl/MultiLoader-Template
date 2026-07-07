package dev.alexcawl.mcmultiloader.metadata

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources
import java.util.LinkedHashMap
import javax.inject.Inject

internal abstract class DefaultMetadataExtension @Inject constructor(
    private val project: Project
) : MetadataExtension {
    override fun template(action: Action<in MetadataExtension.TemplateScope>) {
        action.execute(DefaultTemplateScope(project))
    }

    override fun jarManifest(action: Action<in MetadataExtension.BuilderScope>) {
        val scope = DefaultBuilderScope()
        action.execute(scope)
        val attributes = scope.resolvedValues()
        project.plugins.withType(JavaPlugin::class.java).configureEach {
            project.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar::class.java) {
                manifest.attributes(attributes)
            }
        }
    }
}

private class DefaultTemplateScope(
    private val project: Project
) : MetadataExtension.TemplateScope {
    override fun template(vararg paths: String, action: Action<in MetadataExtension.BuilderScope>) {
        configureTemplates(
            paths.map { path -> project.providers.provider { path } },
            action
        )
    }

    override fun template(path: Provider<String>, action: Action<in MetadataExtension.BuilderScope>) {
        configureTemplates(listOf(path), action)
    }

    private fun configureTemplates(
        paths: List<Provider<String>>,
        action: Action<in MetadataExtension.BuilderScope>
    ) {
        val scope = DefaultBuilderScope()
        action.execute(scope)
        val values = scope.resolvedValues()

        project.plugins.withType(JavaPlugin::class.java).configureEach {
            project.tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, ProcessResources::class.java) {
                paths.forEach { pathProvider ->
                    val path = pathProvider.get()
                    values.forEach { (key, value) ->
                        inputs.property("mcMultiLoader.metadata.template.$path.$key", value)
                    }
                    filesMatching(path) {
                        expand(values)
                    }
                }
            }
        }
    }
}

private class DefaultBuilderScope : MetadataExtension.BuilderScope {
    private val values: MutableMap<String, Any> = LinkedHashMap()

    override fun String.invoke(value: Any) {
        values[this] = value
    }

    fun resolvedValues(): Map<String, Any> = values.mapValues { (_, value) -> resolve(value) }

    private fun resolve(value: Any): Any {
        var current: Any? = value
        while (current is Provider<*>) {
            current = current.orNull
        }
        return current ?: ""
    }
}
