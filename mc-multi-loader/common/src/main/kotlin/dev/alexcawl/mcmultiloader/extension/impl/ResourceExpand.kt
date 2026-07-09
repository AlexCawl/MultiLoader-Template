package dev.alexcawl.mcmultiloader.extension.impl

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.mapProperty
import javax.inject.Inject

internal abstract class ResourceExpand @Inject constructor(
    objects: ObjectFactory,
) {

    internal val patterns: ListProperty<String> = objects.listProperty()

    internal val attributes: MapProperty<String, String> = objects.mapProperty()
}
