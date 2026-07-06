package dev.alexcawl.mcmultiloader

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.jvm.tasks.Jar
import org.gradle.language.jvm.tasks.ProcessResources
import java.util.LinkedHashMap
import javax.inject.Inject

abstract class McMultiLoaderExtension @Inject constructor(
    private val project: Project,
    objects: ObjectFactory
) {
    val resourceTemplates: ResourceTemplates = objects.newInstance(ResourceTemplates::class.java, project)
    val fabricAccessWidener: Property<String> = objects.property(String::class.java)
    val neoForgeAccessTransformer: Property<String> = objects.property(String::class.java)

    fun resourceTemplates(action: Action<in ResourceTemplates>) {
        action.execute(resourceTemplates)
    }

    fun jarManifest(action: Action<in ValueScope>) {
        val scope = ValueScope()
        action.execute(scope)
        val attributes = scope.resolvedValues()
        project.tasks.named("jar", Jar::class.java) {
            manifest.attributes(attributes)
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

        project.tasks.named("processResources", ProcessResources::class.java) {
            values.forEach { (key, value) ->
                inputs.property("mcMultiLoader.resourceTemplate.$path.$key", value)
            }
            filesMatching(path) {
                expand(values)
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
