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
 * Example implementation of [Implement] for use with `ItemStack`s. Not required when using the API;
 * other implementations of `ItemStack`-based `Implement`s are fully allowed.
 */

class Tool private constructor(
    private val persistentAttributed : PersistentAttributable,
    private val itemstackWrapper: Wrapper<ItemStack>,
) : PersistentAttributable by persistentAttributed, Wrapper<ItemStack> by itemstackWrapper {

    @JvmOverloads
    constructor(
        blockworks: Blockworks,
        delegate: ItemStack,
        defaultAttributes : Map<Attribute<*, *>, Any>,
        overwriteAttributes : Boolean = true
    ) : this(
        InternalPersistentAttributed(blockworks, delegate.itemMeta, Tool::class.java, defaultAttributes, overwriteAttributes),
        InternalWrapper(delegate)
        )

    companion object {
        @JvmStatic
        val OF_CONSTRUCTOR : BiFunction<Blockworks, ItemStack, Tool> = BiFunction { blockworks, item ->
            Tool(blockworks, item, emptyMap(), false)
        }
    }

}