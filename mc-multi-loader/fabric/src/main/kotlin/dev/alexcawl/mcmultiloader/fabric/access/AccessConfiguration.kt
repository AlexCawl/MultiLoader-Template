package dev.alexcawl.mcmultiloader.fabric.access

import org.gradle.api.provider.Provider

@AccessDsl
interface AccessConfiguration {

    fun fabricAccessWidener(path: String)

    fun fabricAccessWidener(path: Provider<String>)
}
