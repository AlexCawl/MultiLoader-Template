package dev.alexcawl.mcmultiloader.neoforge.access

import dev.alexcawl.mcmultiloader.neoforge.access.impl.AccessConfigurationImpl
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance

@AccessDsl
interface AccessConfiguration {

    fun neoForgeAccessTransformer(path: String)

    fun neoForgeAccessTransformer(path: Provider<String>)

    companion object {

        fun create(
            project: Project,
            artifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
            directArtifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
            accessTransformers: NamedDomainObjectProvider<ResolvableConfiguration>,
        ): AccessConfiguration {
            return project.objects.newInstance<AccessConfigurationImpl>(
                artifacts,
                directArtifacts,
                accessTransformers,
            )
        }
    }
}
