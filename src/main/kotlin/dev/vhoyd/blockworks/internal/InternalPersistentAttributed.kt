package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.PersistentAttributable
import dev.vhoyd.blockworks.impl.PersistenceWriter
import org.bukkit.persistence.PersistentDataHolder

internal class InternalPersistentAttributed(
    override val blockworks: Blockworks,
    override val persistenceTarget: PersistentDataHolder,
    owner : Class<*>,
    defaultAttributes: Map<Attribute<*, *>, Any>,
    override val overwrite: Boolean,
    val attributed : Attributable = InternalAttributed(owner, defaultAttributes)

) : Attributable by attributed, PersistentAttributable {

    init {
        if (overwrite) {

            @Suppress("UNCHECKED_CAST")
            defaultAttributes.forEach { (key, value) ->
                setAttribute(key as Attribute<Any, Any>, value) }
        }
    }


    override fun <P : Any, C : Any> setAttribute(
        attribute: Attribute<P, C>,
        value: C
    ) {
        attributed.setAttribute(attribute, value)
        println("Setting key $attribute to $value")
        PersistenceWriter.setValue(blockworks.plugin, persistenceTarget, attribute.name, attribute.type, value)
    }

    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C? {
        val value = attributed.getAttribute(attribute)
        value?.let { return it }
        val pValue = PersistenceWriter.getValue(blockworks.plugin, persistenceTarget, attribute.name, attribute.type)
        pValue?.let { attributed.setAttribute(attribute, pValue) }
        return pValue
    }
}