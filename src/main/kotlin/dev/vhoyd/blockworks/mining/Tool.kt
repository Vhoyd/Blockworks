package dev.vhoyd.blockworks.mining

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.nbt.PersistentDataUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


/**
 * Class for adding plugin-unique information about an ItemStack.
 */
class Tool(val blockworks: Blockworks, val item: ItemStack?, data: Map<Attribute<*,*>, Any>, writeData: Boolean = true) : Element<ItemStack>(item ?: ItemStack(Material.AIR), data) {


    /**
     * @param delegate the [ItemStack] representation of the item that players will hold when mining blocks.
     * Should have a count of 1, but do whatever you want IDK
     * @param data the list of attributes that this MiningItem should have, and what the values should be.
     * @param writeData whether the constructor should write the provided data to the underlying [ItemStack]'s
     * [org.bukkit.persistence.PersistentDataContainer]. Recommended for item creation, not recommended if [data]
     * is empty or when preserving existing data values.
     */
    init {
        if (delegate.type != Material.AIR && writeData) {
            val meta = delegate.itemMeta

            delegate.itemMeta = meta
        }
    }

    override fun <P : Any, C : Any> setAttribute(attribute: Attribute<P, C>, value : C) {
        val meta = delegate.itemMeta
        PersistentDataUtil.setTag(blockworks.plugin, meta, "blockworks-${attribute.name}", attribute.persistentDataType, value)
        delegate.itemMeta = meta
    }

    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>) : C {
        return PersistentDataUtil.getTag(blockworks.plugin, delegate.itemMeta, "blockworks-${attribute.name}", attribute.persistentDataType)
    }

}