package dev.vhoyd.blockworks.impl.core

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.PersistentAttributable
import dev.vhoyd.blockworks.api.model.Wrapper
import dev.vhoyd.blockworks.api.pdc.PersistenceWriter
import dev.vhoyd.blockworks.impl.attribute.ProvidedPersistentAttributed
import org.bukkit.inventory.ItemStack
import java.util.function.BiFunction

/**
 * Example implementation of a `Wrapper`/`PersistentAttributable` pair for use with `ItemStack`s.
 *
 * Intended for use with [dev.vhoyd.blockworks.api.model.BlockBreaker.parts].
 * Not required when using the API; other implementations of `ItemStack`-based classes are fully allowed.
 */

class Tool private constructor(
    private val persistentAttributed: PersistentAttributable,
    private val itemstackWrapper: Wrapper<ItemStack>,
    private val overwrite: Boolean,
    private val defaultAttributes: Map<Attribute<*, *>, Any>
) : PersistentAttributable by persistentAttributed, Wrapper<ItemStack> by itemstackWrapper {

    init {
        if (overwrite) {
            @Suppress("UNCHECKED_CAST")
            defaultAttributes.forEach { (key, value) ->
                setAttribute(key as Attribute<Any, Any>, value)
            }
        }
    }

    @JvmOverloads
    constructor(
        blockworks: Blockworks,
        delegate: ItemStack,
        defaultAttributes: Map<Attribute<*, *>, Any>,
        overwriteAttributes: Boolean = true
    ) : this(
        ProvidedPersistentAttributed(blockworks.plugin, delegate.itemMeta, defaultAttributes, overwriteAttributes),
        ProvidedWrapper(delegate),
        overwriteAttributes,
        defaultAttributes,

    )


    override fun <P : Any, C : Any> setAttribute(attribute: Attribute<P, C>, value: C) {
        val meta = delegate.itemMeta
        super.setAttribute(attribute, value)
        PersistenceWriter.setValue(plugin, meta, attribute, value)
        delegate.itemMeta = meta
    }

    companion object {

        /**
         * This field serves as an example of an argument for [Wrapper.validate]; not required for said method.
         */
        @JvmField
        @Suppress("unused") // for external use only
        val VALIDATOR: BiFunction<Blockworks, ItemStack, Tool?> = BiFunction { blockworks, item ->
            if (item.itemMeta == null) return@BiFunction null
            Tool(blockworks, item, emptyMap(), false)
        }
    }

}