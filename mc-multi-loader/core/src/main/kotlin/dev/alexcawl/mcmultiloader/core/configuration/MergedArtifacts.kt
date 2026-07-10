package dev.alexcawl.mcmultiloader.core.configuration

import dev.alexcawl.mcmultiloader.core.McMultiLoaderConstants
import java.io.File
import java.util.zip.ZipFile

fun selectMergedArtifacts(
    artifacts: Iterable<File>,
    directArtifacts: Iterable<File>,
): List<File> {
    val directPaths = directArtifacts.map(File::normalizedPath).toSet()
    return artifacts.filter { artifact ->
        artifact.normalizedPath() in directPaths || artifact.hasCommonDescriptor()
    }
}

private fun File.hasCommonDescriptor(): Boolean = isFile && runCatching {
    ZipFile(this).use { zip -> zip.getEntry(McMultiLoaderConstants.DESCRIPTOR_PATH) != null }
}.getOrDefault(false)

private fun File.normalizedPath() = toPath().toAbsolutePath().normalize()
