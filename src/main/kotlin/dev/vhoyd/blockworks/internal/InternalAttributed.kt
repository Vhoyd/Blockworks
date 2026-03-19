package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attribute

internal class InternalAttributed(defaultAttributes: Map<Attribute<*, *>, Any>) : Attributable {

    override val attributes: MutableMap<Attribute<*, *>, Any> = defaultAttributes.toMutableMap()

}