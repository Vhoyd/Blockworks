package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.PersistentAttributable
import dev.vhoyd.blockworks.impl.PersistenceWriter
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.plugin.Plugin

/**
 * Default [PersistentAttributable] implementation.
 *
 * Uses both an accessible `Map` and a `PersistentDataHolder` to read/write data.
 * Intended for single-entity use only.
 *
 */
internal class InternalPersistentAttributed(
    override val plugin: Plugin,
    override val persistenceTarget: PersistentDataHolder,
    defaultAttributes: Map<Attribute<*, *>, Any>,
    overwrite: Boolean,
    val attributed: Attributable = InternalAttributed(defaultAttributes)

) : Attributable by attributed, PersistentAttributable {

    init {
        if (overwrite) {
            @Suppress("UNCHECKED_CAST")
            defaultAttributes.forEach { (key, value) ->
                setAttribute(key as Attribute<Any, Any>, value)
            }
        }
    }


    override fun <P : Any, C : Any> setAttribute(
        attribute: Attribute<P, C>,
        value: C
    ) {
        attributed.setAttribute(attribute, value)
        PersistenceWriter.setValue(plugin, persistenceTarget, attribute.name, attribute.type, value)
    }

    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C? {
        val value = attributed.getAttribute(attribute)
        value?.let { return it }
        val pValue = PersistenceWriter.getValue(plugin, persistenceTarget, attribute.name, attribute.type)
        pValue?.let { attributed.setAttribute(attribute, pValue) }
        return pValue
    }
}