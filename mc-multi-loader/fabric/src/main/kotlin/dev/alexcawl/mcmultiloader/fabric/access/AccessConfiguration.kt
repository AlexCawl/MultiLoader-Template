package dev.alexcawl.mcmultiloader.fabric.access

import dev.alexcawl.mcmultiloader.fabric.access.impl.AccessConfigurationImpl
import dev.alexcawl.mcmultiloader.fabric.access.impl.accessFeature
import org.gradle.api.Action
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

        fun apply(
            project: Project,
            action: Action<in AccessConfiguration>,
            accessWideners: NamedDomainObjectProvider<ResolvableConfiguration>,
        ) {
            val configuration = project.objects.newInstance<AccessConfigurationImpl>()
            action.execute(configuration)
            with(project) {
                with(configuration) {
                    accessFeature(fabricAccessWidener, accessWideners)
                }
            }
        }
    }
}
