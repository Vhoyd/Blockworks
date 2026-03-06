package dev.vhoyd.blockworks.impl

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.model.Attribute
import dev.vhoyd.blockworks.model.AttributedImplement
import dev.vhoyd.blockworks.nbt.PersistenceWriter
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import java.util.function.BiFunction
import java.util.function.Function

/**
 * Class for adding plugin-unique information about an ItemStack.
 */
class Tool @JvmOverloads constructor(
    val blockworks: Blockworks,
    item: ItemStack?,
    data: Map<Attribute<*, *>, Any>,
    overwriteData: Boolean = true
) : AttributedImplement<ItemStack>(item ?: ItemStack(Material.AIR), data, overwriteData) {

    init {
        if (delegate.type != Material.AIR && overwriteData) {
            val meta = delegate.itemMeta
            delegate.itemMeta = meta
        }
    }

    override fun <P : Any, C : Any> setAttribute(attribute: Attribute<P, C>, value : C) {
        val meta = delegate.itemMeta
        PersistenceWriter.setTag(blockworks.plugin, meta, "blockworks-${attribute.name}", attribute.persistentDataType, value)
        delegate.itemMeta = meta
    }

    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>) : C {
        return PersistenceWriter.getTag(blockworks.plugin, delegate.itemMeta, "blockworks-${attribute.name}", attribute.persistentDataType)
    }

    companion object {
        @JvmStatic
        val OF_CONSTRUCTOR : BiFunction<Blockworks, ItemStack, Tool> = BiFunction { blockworks, item -> Tool(blockworks, item, emptyMap(), false) }
    }

}