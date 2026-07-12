package dev.alexcawl.mcmultiloader.core.attributes

import org.gradle.api.attributes.Attribute

enum class LoaderType {
    FABRIC,
    NEOFORGE;

    companion object {

        const val ATTRIBUTE_NAME = "dev.alexcawl.minecraft.loader"

        val ATTRIBUTE: Attribute<LoaderType> = Attribute.of(ATTRIBUTE_NAME, LoaderType::class.java)
    }
}
