package dev.vhoyd.blockworks.impl

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.PersistentAttributable
import dev.vhoyd.blockworks.api.model.Wrapper
import dev.vhoyd.blockworks.internal.InternalWrapper
import dev.vhoyd.blockworks.internal.InternalPersistentAttributed
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
) : PersistentAttributable by persistentAttributed, Wrapper<ItemStack> by itemstackWrapper {

    @JvmOverloads
    constructor(
        blockworks: Blockworks,
        delegate: ItemStack,
        defaultAttributes: Map<Attribute<*, *>, Any>,
        overwriteAttributes: Boolean = true
    ) : this(
        InternalPersistentAttributed(blockworks.plugin, delegate.itemMeta, defaultAttributes, overwriteAttributes),
        InternalWrapper(delegate)
    )

    companion object {

        /**
         * This field serves as an example of an argument for [Wrapper.validate]; not required for said method.
         */
        @JvmField
        @Suppress("unused") // for external use only
        val OF_CONSTRUCTOR: BiFunction<Blockworks, ItemStack, Tool?> = BiFunction { blockworks, item ->
            if (item.itemMeta == null) return@BiFunction null
            Tool(blockworks, item, emptyMap(), false)
        }
    }

}