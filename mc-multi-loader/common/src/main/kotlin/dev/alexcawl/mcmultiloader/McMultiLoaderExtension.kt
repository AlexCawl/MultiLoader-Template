package dev.alexcawl.mcmultiloader

import org.gradle.api.provider.Property

interface McMultiLoaderExtension {
    val fabricAccessWidener: Property<String>
    val neoForgeAccessTransformer: Property<String>
}
