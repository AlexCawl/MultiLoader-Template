package dev.alexcawl.mcmultiloader.core.extension

import org.gradle.api.Action
import org.gradle.api.provider.Provider

@MetadataDsl
interface MetadataExtension {

    fun resources(action: Action<in ResourceScope>)

    fun jarManifest(action: Action<in BuilderScope>)

    @MetadataDsl
    interface ResourceScope {

        fun resource(vararg patterns: String, action: Action<in BuilderScope>)
    }

    @MetadataDsl
    interface BuilderScope {

        operator fun String.invoke(value: String)

        operator fun String.invoke(value: Provider<String>)
    }
}
