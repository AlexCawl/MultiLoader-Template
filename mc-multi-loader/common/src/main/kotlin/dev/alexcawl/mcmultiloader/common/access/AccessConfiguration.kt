package dev.alexcawl.mcmultiloader.common.access

import dev.alexcawl.mcmultiloader.common.access.impl.AccessConfigurationImpl
import dev.alexcawl.mcmultiloader.common.access.impl.accessFeature
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance

@AccessDsl
interface AccessConfiguration {

    fun fabricAccessWidener(path: String)

    fun fabricAccessWidener(path: Provider<String>)

    fun neoForgeAccessTransformer(path: String)

    fun neoForgeAccessTransformer(path: Provider<String>)

    companion object {

        fun apply(
            project: Project,
            action: Action<in AccessConfiguration>,
            mmlApi: NamedDomainObjectProvider<DependencyScopeConfiguration>,
            mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
        ) {
            val configuration = project.objects.newInstance<AccessConfigurationImpl>()
            action.execute(configuration)
            with(project) {
                with(configuration) {
                    accessFeature(fabricAccessWidener, neoForgeAccessTransformer, mmlApi, mmlImplementation)
                }
            }
        }
    }
}
