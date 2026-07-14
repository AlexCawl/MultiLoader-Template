package dev.alexcawl.mcmultiloader.common.extension.impl

import dev.alexcawl.mcmultiloader.common.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.common.extension.McCommonLoaderExtension
import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import javax.inject.Inject

internal abstract class McCommonLoaderExtensionImpl @Inject constructor(
    private val project: Project,
    private val mmlApi: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    private val mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
) : McCommonLoaderExtension {

    override fun metadata(action: Action<in MetadataConfiguration>) {
        MetadataConfiguration.apply(project, action)
    }

    override fun access(action: Action<in AccessConfiguration>) {
        AccessConfiguration.apply(project, action, mmlApi, mmlImplementation)
    }
}
