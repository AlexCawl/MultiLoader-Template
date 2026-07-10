package dev.alexcawl.mcmultiloader.core

object McMultiLoaderConstants {
    const val MERGED_CONFIGURATION_NAME = "merged"
    const val MERGED_ARTIFACT_CONFIGURATION_NAME = "mergedArtifact"
    const val DIRECT_MERGED_ARTIFACT_CONFIGURATION_NAME = "directMergedArtifact"
    const val MERGED_CONFIGURATION_DESCRIPTION = "Common modules merged into this loader artifact."

    const val DESCRIPTOR_PATH = "META-INF/mc-multi-loader/common.properties"
    const val FABRIC_ACCESS_WIDENER_PROPERTY = "fabricAccessWidener"
    const val NEOFORGE_ACCESS_TRANSFORMER_PROPERTY = "neoForgeAccessTransformer"

    const val MERGED_DEPENDENCY_TYPE_ERROR =
        "Configuration '%s' only supports project or external module dependencies."
}
