package dev.alexcawl.mcmultiloader.core.metadata

import dev.alexcawl.mcmultiloader.core.metadata.impl.MetadataConfigurationImpl
import dev.alexcawl.mcmultiloader.core.metadata.impl.metadataFeature
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance

@MetadataDsl
interface MetadataConfiguration {

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

    companion object {

        fun apply(project: Project, action: Action<in MetadataConfiguration>) {
            val configuration = project.objects.newInstance<MetadataConfigurationImpl>()
            action.execute(configuration)
            with(project) {
                with(configuration) {
                    metadataFeature(resourcesExpands, jarManifestAttributes)
                }
            }
        }
    }
}
