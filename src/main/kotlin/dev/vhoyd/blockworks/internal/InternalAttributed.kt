package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attributable.Companion.INTERNAL_CLASS_FLAG
import dev.vhoyd.blockworks.api.model.Attribute

internal class InternalAttributed(owner: Class<*>, defaultAttributes : Map<Attribute<*, *>, Any>) : Attributable {

    override val attributes: MutableMap<Attribute<*, *>, Any> = defaultAttributes.toMutableMap()

    init {
        setAttribute(INTERNAL_CLASS_FLAG, owner.simpleName)
    }

    override fun <P : Any, C : Any> setAttribute(
        attribute: Attribute<P, C>,
        value: C
    ) {
        attributes[attribute] = value
    }

    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C? {
        val value = attributes[attribute]
        val c = attribute.type.complexType
        return if (c.isInstance(value)) c.cast(value) else null
    }
}