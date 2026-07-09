package dev.alexcawl.mcmultiloader.core.feature

import org.gradle.api.GradleException
import java.io.File
import java.util.Properties
import java.util.zip.ZipFile

internal data class FabricAccessWidener(
    val source: String,
    val header: String,
    val body: List<String>,
)

internal data class MissingFabricAccessWidenerLine(
    val source: String,
    val line: String,
)

internal fun readFabricAccessWidener(artifact: File): FabricAccessWidener? = ZipFile(artifact).use { zip ->
    val properties = zip.readDescriptor() ?: return null
    val path = properties.getProperty(FABRIC_ACCESS_WIDENER_PROPERTY) ?: return null
    val entry = zip.getEntry(path)
        ?: throw GradleException("Fabric access widener '$path' declared by '${artifact.name}' was not found.")
    zip.getInputStream(entry).bufferedReader().use { reader ->
        readFabricAccessWidener("${artifact.name}!$path", reader.readText())
    }
}

internal fun readFabricAccessWidener(source: String, content: String): FabricAccessWidener {
    val lines = content.lines()
    val headerIndex = lines.indexOfFirst(::isFabricAccessWidenerHeader)
    if (headerIndex < 0) {
        throw GradleException("Fabric access widener '$source' has no header.")
    }
    return FabricAccessWidener(source, lines[headerIndex], lines.drop(headerIndex + 1))
}

internal fun validateFabricAccessWidener(
    actual: FabricAccessWidener,
    expected: List<FabricAccessWidener>,
) {
    expected.firstOrNull { it.header != actual.header }?.let { mismatch ->
        throw GradleException(
            "Fabric access widener header mismatch in '${mismatch.source}': " +
                "expected '${actual.header}', found '${mismatch.header}'."
        )
    }

    val actualLines = actual.significantLines().toSet()
    val missing = expected.flatMap { accessWidener ->
        accessWidener.significantLines()
            .filter { it !in actualLines }
            .map { MissingFabricAccessWidenerLine(accessWidener.source, it) }
    }
    if (missing.isNotEmpty()) {
        throw GradleException(buildMissingLinesMessage(actual.source, missing))
    }
}

private fun ZipFile.readDescriptor(): Properties? {
    val entry = getEntry(DESCRIPTOR_PATH) ?: return null
    return Properties().also { properties ->
        getInputStream(entry).use(properties::load)
    }
}

private fun isFabricAccessWidenerHeader(line: String): Boolean {
    val trimmed = line.trim()
    return trimmed.isNotEmpty() && !trimmed.startsWith("#")
}

private fun FabricAccessWidener.significantLines(): List<String> =
    body.mapNotNull(::significantLine)

private fun significantLine(line: String): String? {
    val trimmed = line.trim()
    return trimmed.takeIf { it.isNotEmpty() && !it.startsWith("#") }
}

private fun buildMissingLinesMessage(
    actualSource: String,
    missing: List<MissingFabricAccessWidenerLine>,
): String = buildString {
    appendLine("Fabric access widener '$actualSource' is missing entries required by merged common artifacts:")
    missing.take(20).forEach { missingLine ->
        appendLine(" - ${missingLine.source}: ${missingLine.line}")
    }
    if (missing.size > 20) {
        appendLine(" - ... ${missing.size - 20} more")
    }
}
