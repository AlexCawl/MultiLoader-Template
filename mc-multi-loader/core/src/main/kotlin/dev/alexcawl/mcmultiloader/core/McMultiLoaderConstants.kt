package dev.alexcawl.mcmultiloader.core

object McMultiLoaderConstants {

    object Configuration {

        const val MERGED = "merged"

        const val MERGED_DESCRIPTION = "Common modules merged into this loader artifact."

        const val MERGED_ARTIFACT = "mergedArtifact"

        const val MERGED_ARTIFACT_DESCRIPTION = "Resolved runtime artifacts from merged dependencies, including transitive dependencies."

        const val DIRECT_MERGED_ARTIFACT = "directMergedArtifact"

        const val DIRECT_MERGED_ARTIFACT_DESCRIPTION = "Resolved direct runtime artifacts from merged dependencies."

        const val FABRIC_ACCESS_WIDENER_ELEMENTS = "fabricAccessWidenerElements"

        const val FABRIC_ACCESS_WIDENER_ELEMENTS_DESCRIPTION = "Fabric access widener published by a common module."

        const val NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS = "neoForgeAccessTransformerElements"

        const val NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS_DESCRIPTION = "NeoForge access transformer published by a common module."

        const val FABRIC_ACCESS_WIDENER_CLASSPATH = "fabricAccessWidenerClasspath"

        const val FABRIC_ACCESS_WIDENER_CLASSPATH_DESCRIPTION = "Fabric access wideners resolved from merged dependencies."

        const val NEOFORGE_ACCESS_TRANSFORMER_CLASSPATH = "neoForgeAccessTransformerClasspath"

        const val NEOFORGE_ACCESS_TRANSFORMER_CLASSPATH_DESCRIPTION = "NeoForge access transformers resolved from merged dependencies."
    }

    const val MERGED_DEPENDENCY_TYPE_ERROR =
        "Configuration '%s' only supports project or external module dependencies."
}
