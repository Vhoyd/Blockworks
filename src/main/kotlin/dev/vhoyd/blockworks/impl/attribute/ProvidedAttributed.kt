package dev.vhoyd.blockworks.impl.attribute

import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attribute

internal class ProvidedAttributed(defaultAttributes: Map<Attribute<*, *>, Any>) : Attributable {

    override val attributes: MutableMap<Attribute<*, *>, Any> = defaultAttributes.toMutableMap()

}