package dev.alexcawl.mcmultiloader.access

import org.gradle.api.provider.Provider

@AccessDsl
interface AccessExtension {

    fun fabricAccessWidener(path: String)

    fun fabricAccessWidener(path: Provider<String>)

    fun neoForgeAccessTransformer(path: String)

    fun neoForgeAccessTransformer(path: Provider<String>)
}
