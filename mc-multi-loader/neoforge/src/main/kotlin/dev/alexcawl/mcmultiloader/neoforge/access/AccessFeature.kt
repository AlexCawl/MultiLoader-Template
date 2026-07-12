package dev.alexcawl.mcmultiloader.neoforge.access

import dev.alexcawl.mcmultiloader.neoforge.access.impl.AccessFeatureImpl
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.kotlin.dsl.newInstance

interface AccessFeature {

    val configuration: AccessConfiguration

    fun install(
        artifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        directArtifacts: NamedDomainObjectProvider<ResolvableConfiguration>,
        accessTransformers: NamedDomainObjectProvider<ResolvableConfiguration>,
    )

    companion object {

        fun create(project: Project): AccessFeature {
            return project.objects.newInstance<AccessFeatureImpl>()
        }
    }
}
