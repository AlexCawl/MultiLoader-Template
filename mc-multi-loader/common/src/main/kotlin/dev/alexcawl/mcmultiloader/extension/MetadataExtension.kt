package dev.alexcawl.mcmultiloader.extension

import org.gradle.api.Action
import org.gradle.api.provider.Provider

@MetadataDsl
interface MetadataExtension {

    fun template(action: Action<in TemplateScope>)

    fun jarManifest(action: Action<in BuilderScope>)

    @MetadataDsl
    interface TemplateScope {

        fun template(vararg paths: String, action: Action<in BuilderScope>)

        fun template(path: Provider<String>, action: Action<in BuilderScope>)
    }

    @MetadataDsl
    interface BuilderScope {

        operator fun String.invoke(value: String)

        operator fun String.invoke(value: Provider<String>)
    }
}
