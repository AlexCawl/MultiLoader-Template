package dev.alexcawl.mcmultiloader.common.access

import org.gradle.api.provider.Provider

@AccessDsl
interface AccessConfiguration {

    fun fabricAccessWidener(path: String)

    fun fabricAccessWidener(path: Provider<String>)

    fun neoForgeAccessTransformer(path: String)

    fun neoForgeAccessTransformer(path: Provider<String>)
}
