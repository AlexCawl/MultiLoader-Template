package dev.alexcawl.mcmultiloader.neoforge.extension.impl

import dev.alexcawl.mcmultiloader.core.metadata.MetadataConfiguration
import dev.alexcawl.mcmultiloader.neoforge.access.AccessConfiguration
import dev.alexcawl.mcmultiloader.neoforge.extension.McNeoForgeLoaderExtension
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.ResolvableConfiguration
import javax.inject.Inject

internal abstract class McNeoForgeLoaderExtensionImpl @Inject constructor(
    private val project: Project,
    private val neoForgeAccessTransformerClasspath: NamedDomainObjectProvider<ResolvableConfiguration>,
) : McNeoForgeLoaderExtension {

    override fun metadata(action: Action<in MetadataConfiguration>) {
        MetadataConfiguration.apply(project, action)
    }

    override fun access(action: Action<in AccessConfiguration>) {
        AccessConfiguration.apply(project, action, neoForgeAccessTransformerClasspath)
    }
}
