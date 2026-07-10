package dev.alexcawl.mcmultiloader.common.access

import dev.alexcawl.mcmultiloader.common.access.impl.AccessFeatureImpl
import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance

interface AccessFeature {

    val configuration: AccessConfiguration

    fun install()

    companion object {

        fun create(project: Project): AccessFeature {
            return project.objects.newInstance<AccessFeatureImpl>()
        }
    }
}
