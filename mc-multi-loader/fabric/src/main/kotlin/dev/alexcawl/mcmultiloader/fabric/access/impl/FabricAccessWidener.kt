package dev.alexcawl.mcmultiloader.fabric.access.impl

import org.gradle.api.GradleException

internal data class FabricAccessWidener(
    val source: String,
    val header: String,
    val body: List<String>,
)

private data class MissingLine(
    val source: String,
    val line: String,
)

internal fun readFabricAccessWidener(source: String, content: String): FabricAccessWidener {
    val lines = content.lines()
    val headerIndex = lines.indexOfFirst(::isHeader)
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
            .filterNot(actualLines::contains)
            .map { MissingLine(accessWidener.source, it) }
    }
    if (missing.isNotEmpty()) {
        throw GradleException(buildMissingLinesMessage(actual.source, missing))
    }
}

private fun isHeader(line: String): Boolean = line.trim().let { it.isNotEmpty() && !it.startsWith("#") }

private fun FabricAccessWidener.significantLines(): List<String> = body.mapNotNull { line ->
    line.trim().takeIf { it.isNotEmpty() && !it.startsWith("#") }
}

private fun buildMissingLinesMessage(actualSource: String, missing: List<MissingLine>): String = buildString {
    appendLine("Fabric access widener '$actualSource' is missing entries required by MML common artifacts:")
    missing.take(20).forEach { appendLine(" - ${it.source}: ${it.line}") }
    if (missing.size > 20) appendLine(" - ... ${missing.size - 20} more")
}
