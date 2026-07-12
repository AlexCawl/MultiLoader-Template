package dev.alexcawl.mcmultiloader.common.access

import dev.alexcawl.mcmultiloader.common.access.impl.AccessConfigurationImpl
import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.newInstance

@AccessDsl
interface AccessConfiguration {

    fun fabricAccessWidener(path: String)

    fun fabricAccessWidener(path: Provider<String>)

    fun neoForgeAccessTransformer(path: String)

    fun neoForgeAccessTransformer(path: Provider<String>)

    companion object {

        fun create(project: Project): AccessConfiguration {
            return project.objects.newInstance<AccessConfigurationImpl>()
        }
    }
}
