package dev.alexcawl.mcmultiloader.neoforge.access

import org.gradle.api.provider.Provider

@AccessDsl
interface AccessConfiguration {

    fun neoForgeAccessTransformer(path: String)

    fun neoForgeAccessTransformer(path: Provider<String>)
}
