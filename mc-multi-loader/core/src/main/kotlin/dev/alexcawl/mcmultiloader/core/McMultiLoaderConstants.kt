package dev.alexcawl.mcmultiloader.core

object McMultiLoaderConstants {

    object Configuration {

        const val FABRIC_ACCESS_WIDENER_ELEMENTS = "fabricAccessWidenerElements"

        const val FABRIC_ACCESS_WIDENER_ELEMENTS_DESCRIPTION = "Fabric access widener published by a common module."

        const val NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS = "neoForgeAccessTransformerElements"

        const val NEOFORGE_ACCESS_TRANSFORMER_ELEMENTS_DESCRIPTION = "NeoForge access transformer published by a common module."

        const val FABRIC_ACCESS_WIDENER_CLASSPATH = "fabricAccessWidenerClasspath"

        const val FABRIC_ACCESS_WIDENER_CLASSPATH_DESCRIPTION = "Fabric access wideners resolved from MML dependencies."

        const val NEOFORGE_ACCESS_TRANSFORMER_CLASSPATH = "neoForgeAccessTransformerClasspath"

        const val NEOFORGE_ACCESS_TRANSFORMER_CLASSPATH_DESCRIPTION =
            "NeoForge access transformers resolved from MML dependencies."
    }
}
