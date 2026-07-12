package dev.alexcawl.mcmultiloader.core.attributes

import org.gradle.api.attributes.Attribute

enum class AccessModifierType {
    ACCESS_WIDENER,
    ACCESS_TRANSFORMER;

    companion object {

        const val ATTRIBUTE_NAME = "dev.alexcawl.minecraft.accessModifier"

        val ATTRIBUTE: Attribute<AccessModifierType> =
            Attribute.of(ATTRIBUTE_NAME, AccessModifierType::class.java)
    }
}
