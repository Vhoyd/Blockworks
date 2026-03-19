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

) :  PersistentAttributable {

    override val attributes = defaultAttributes.toMutableMap()

    init {
        if (overwrite) {
            @Suppress("UNCHECKED_CAST")
            defaultAttributes.forEach { (key, value) ->
                setAttribute(key as Attribute<Any, Any>, value)
            }
        }
    }

}