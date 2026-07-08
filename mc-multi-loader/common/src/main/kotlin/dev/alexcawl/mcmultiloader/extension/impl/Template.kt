package dev.alexcawl.mcmultiloader.extension.impl

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.kotlin.dsl.mapProperty
import javax.inject.Inject

internal abstract class Template @Inject constructor(
    objects: ObjectFactory,
) {

    internal val files: ConfigurableFileCollection = objects.fileCollection()

    internal val attributes: MapProperty<String, String> = objects.mapProperty(String::class, String::class)
}
