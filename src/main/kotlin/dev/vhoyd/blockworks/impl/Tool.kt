package dev.vhoyd.blockworks.impl

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.model.Attribute
import dev.vhoyd.blockworks.model.AttributedImplement
import dev.vhoyd.blockworks.nbt.PersistenceWriter
import org.bukkit.inventory.ItemStack
import java.util.function.BiFunction

/**
 * Example implementation of [AttributedImplement] for use with `ItemStack`s. Not required when using the API;
 * other implementations of `ItemStack`-based `AttributedImplement`s are fully allowed.
 */
class Tool @JvmOverloads constructor(
    val blockworks: Blockworks,
    item: ItemStack?,
    data: Map<Attribute<*, *>, Any>,
    overwriteData: Boolean = true
) : AttributedImplement<ItemStack>(item ?: ItemStack.empty(), data, overwriteData) {


    override fun <P : Any, C : Any> setAttribute(attribute: Attribute<P, C>, value : C) {
        val meta = delegate.itemMeta
        PersistenceWriter.setValue(blockworks.plugin, meta, "blockworks-${attribute.name}", attribute.persistentDataType, value)
        delegate.itemMeta = meta
    }


    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C? {
        if (delegate.itemMeta == null) return null
        return PersistenceWriter.getValue(blockworks.plugin, delegate.itemMeta, "blockworks-${attribute.name}", attribute.persistentDataType)
    }

    companion object {
        @JvmStatic
        val OF_CONSTRUCTOR : BiFunction<Blockworks, ItemStack, out Tool> = BiFunction { blockworks, item ->
            val tool = Tool(blockworks, item, emptyMap(), false)
            tool.applyAttributes()
            tool
        }
    }

}