package dev.alexcawl.mcmultiloader.metadata

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources
import java.util.LinkedHashMap
import javax.inject.Inject

interface MetadataExtension {
    val resourceTemplates: ResourceTemplates

    fun resourceTemplates(action: Action<in ResourceTemplates>)

    fun jarManifest(action: Action<in ValueScope>)
}

internal abstract class DefaultMetadataExtension @Inject constructor(
    private val project: Project,
    objects: ObjectFactory
) : MetadataExtension {
    override val resourceTemplates: ResourceTemplates = objects.newInstance(ResourceTemplates::class.java, project)

    override fun resourceTemplates(action: Action<in ResourceTemplates>) {
        action.execute(resourceTemplates)
    }

    override fun jarManifest(action: Action<in ValueScope>) {
        val scope = ValueScope()
        action.execute(scope)
        val attributes = scope.resolvedValues()
        project.plugins.withType(JavaPlugin::class.java).configureEach {
            project.tasks.named(JavaPlugin.JAR_TASK_NAME, Jar::class.java) {
                manifest.attributes(attributes)
            }
        }
    }
}

open class ResourceTemplates @Inject constructor(private val project: Project) {
    fun loaderManifest(path: String, action: Action<in ValueScope>) {
        template(path, action)
    }

    fun mixinConfig(path: String, action: Action<in ValueScope>) {
        template(path, action)
    }

    fun template(path: String, action: Action<in ValueScope>) {
        val scope = ValueScope()
        action.execute(scope)
        val values = scope.resolvedValues()

        project.plugins.withType(JavaPlugin::class.java).configureEach {
            project.tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, ProcessResources::class.java) {
                values.forEach { (key, value) ->
                    inputs.property("mcMultiLoader.metadata.resourceTemplate.$path.$key", value)
                }
                filesMatching(path) {
                    expand(values)
                }
            }
        }
    }
}

class ValueScope {
    private val values: MutableMap<String, Any> = LinkedHashMap()

    operator fun String.invoke(value: Any) {
        values[this] = value
    }

    internal fun resolvedValues(): Map<String, Any> = values.mapValues { (_, value) -> resolve(value) }

    private fun resolve(value: Any): Any {
        var current: Any? = value
        while (current is Provider<*>) {
            current = current.orNull
        }
        return current ?: ""
    }
}
