package dev.alexcawl.mcmultiloader.core

import org.gradle.api.attributes.Attribute

enum class LoaderType {
    ALL,
    FABRIC,
    NEOFORGE;

    companion object {

        const val ATTRIBUTE_NAME = "dev.alexcawl.mcmultiloader.loaderType"

        val ATTRIBUTE: Attribute<LoaderType> = Attribute.of<LoaderType>(ATTRIBUTE_NAME, LoaderType::class.java)
    }
}
