package dev.alexcawl.multiloader

import org.gradle.api.attributes.Attribute

internal const val MERGED_USAGE: String = "dev.alexcawl.multiloader.merged"

internal enum class MergedSource {
    JAVA,
    RESOURCE;

    companion object {
        val ATTRIBUTE: Attribute<MergedSource> =
            Attribute.of("dev.alexcawl.multiloader.merged.source", MergedSource::class.java)
    }
}
