package dev.alexcawl.mcmultiloader.common

import dev.alexcawl.mcmultiloader.common.configuration.configureMmlApi
import dev.alexcawl.mcmultiloader.common.configuration.configureMmlImplementation
import dev.alexcawl.mcmultiloader.common.configuration.configureMmlRuntimeElements
import dev.alexcawl.mcmultiloader.common.configuration.mmlApi
import dev.alexcawl.mcmultiloader.common.configuration.mmlImplementation
import dev.alexcawl.mcmultiloader.common.configuration.mmlRuntimeElements
import dev.alexcawl.mcmultiloader.common.extension.McCommonLoaderExtension
import dev.alexcawl.mcmultiloader.common.extension.impl.McCommonLoaderExtensionImpl
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.ConsumableConfiguration
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.newInstance

class CommonPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val mmlApi: NamedDomainObjectProvider<DependencyScopeConfiguration> = target.mmlApi()
        target.configureMmlApi(mmlApi)
        val mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration> = target.mmlImplementation()
        target.configureMmlImplementation(mmlImplementation)
        val mmlRuntimeElements: NamedDomainObjectProvider<ConsumableConfiguration> = target.mmlRuntimeElements(mmlApi, mmlImplementation)
        target.configureMmlRuntimeElements(mmlRuntimeElements)
        val extension = target.objects.newInstance<McCommonLoaderExtensionImpl>(mmlApi, mmlImplementation)
        target.extensions.add<McCommonLoaderExtension>(EXTENSION_NAME, extension)
    }

    companion object {

        const val EXTENSION_NAME = "mcCommonLoader"
    }
}
