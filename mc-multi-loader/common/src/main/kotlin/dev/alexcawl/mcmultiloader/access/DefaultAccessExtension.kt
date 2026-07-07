package dev.alexcawl.mcmultiloader.access

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

internal abstract class DefaultAccessExtension @Inject constructor(
    objects: ObjectFactory,
) : AccessExtension {

    internal val fabricAccessWidenerPath: Property<String> = objects.property(String::class)

    internal val neoForgeAccessTransformerPath: Property<String> = objects.property(String::class)

    override fun fabricAccessWidener(path: String) {
        fabricAccessWidenerPath.set(path)
    }

    override fun fabricAccessWidener(path: Provider<String>) {
        fabricAccessWidenerPath.set(path)
    }

    override fun neoForgeAccessTransformer(path: String) {
        neoForgeAccessTransformerPath.set(path)
    }

    override fun neoForgeAccessTransformer(path: Provider<String>) {
        neoForgeAccessTransformerPath.set(path)
    }
}
