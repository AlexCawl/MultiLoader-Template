package dev.alexcawl.metadata

interface MetadataExtension {
    fun resources(vararg patterns: String, block: Scope.() -> Unit)

    fun jarManifest(block: Scope.() -> Unit)

    interface Scope {
        operator fun String.invoke(value: Any)
    }
}
