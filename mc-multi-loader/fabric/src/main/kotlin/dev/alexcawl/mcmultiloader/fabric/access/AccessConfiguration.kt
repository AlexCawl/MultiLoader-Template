package dev.alexcawl.mcmultiloader.fabric.access

import dev.alexcawl.mcmultiloader.fabric.access.impl.AccessConfigurationImpl
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance

@AccessDsl
interface AccessConfiguration {

    fun fabricAccessWidener(path: String)

    fun fabricAccessWidener(path: Provider<String>)

    companion object {

        fun create(
            project: Project,
            accessWideners: NamedDomainObjectProvider<ResolvableConfiguration>,
        ): AccessConfiguration {
            return project.objects.newInstance<AccessConfigurationImpl>(
                accessWideners,
            )
        }
    }
}
