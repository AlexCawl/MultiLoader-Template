package dev.alexcawl.mcmultiloader.neoforge.configuration

import org.gradle.api.NamedDomainObjectProvider
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyScopeConfiguration
import org.gradle.api.artifacts.ResolvableConfiguration
import org.gradle.api.plugins.JavaPlugin
import org.gradle.language.jvm.tasks.ProcessResources

private const val MANIFEST_EXCLUDE = "META-INF/MANIFEST.MF"
private const val SIGNATURE_EXCLUDE = "META-INF/*.SF"
private const val RSA_SIGNATURE_EXCLUDE = "META-INF/*.RSA"
private const val DSA_SIGNATURE_EXCLUDE = "META-INF/*.DSA"
private const val INTERNAL_METADATA_EXCLUDE = "META-INF/mc-multi-loader/**"

internal fun Project.configureMmlRuntimeEmbedding(
    mmlImplementation: NamedDomainObjectProvider<DependencyScopeConfiguration>,
    mmlRuntimeClasspath: NamedDomainObjectProvider<ResolvableConfiguration>,
) {
    val mmlRuntimeTrees = providers.provider {
        mmlRuntimeClasspath.get().files.map(::zipTree)
    }

    plugins.withType(JavaPlugin::class.java).configureEach {
        configurations.named(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME) {
            extendsFrom(mmlImplementation.get())
        }
        tasks.named(JavaPlugin.PROCESS_RESOURCES_TASK_NAME, ProcessResources::class.java) {
            dependsOn(mmlRuntimeClasspath)
            from(mmlRuntimeTrees) {
                exclude(MANIFEST_EXCLUDE)
                exclude(SIGNATURE_EXCLUDE)
                exclude(RSA_SIGNATURE_EXCLUDE)
                exclude(DSA_SIGNATURE_EXCLUDE)
                exclude(INTERNAL_METADATA_EXCLUDE)
            }
        }
    }
}
