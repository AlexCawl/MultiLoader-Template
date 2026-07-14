package dev.alexcawl.mcmultiloader.neoforge.access

import dev.alexcawl.mcmultiloader.neoforge.access.impl.AccessConfigurationImpl
import dev.alexcawl.mcmultiloader.neoforge.access.impl.accessFeature
import org.gradle.api.Action
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

        fun apply(
            project: Project,
            action: Action<in AccessConfiguration>,
            neoForgeAccessTransformerClasspath: NamedDomainObjectProvider<ResolvableConfiguration>,
        ) {
            val configuration = project.objects.newInstance<AccessConfigurationImpl>()
            action.execute(configuration)
            with(project) {
                with(configuration) {
                    accessFeature(neoForgeAccessTransformer, neoForgeAccessTransformerClasspath)
                }
            }
        }
    }
}
