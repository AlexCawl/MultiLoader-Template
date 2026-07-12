package dev.alexcawl.mcmultiloader.core.configuration

import java.io.File

fun selectMergedArtifacts(
    artifacts: Iterable<File>,
    directArtifacts: Iterable<File>,
): List<File> {
    val directPaths = directArtifacts.map(File::normalizedPath).toSet()
    return artifacts.filter { artifact ->
        artifact.normalizedPath() in directPaths
    }
}

private fun File.normalizedPath() = toPath().toAbsolutePath().normalize()
