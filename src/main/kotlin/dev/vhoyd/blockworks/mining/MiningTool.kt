package dev.vhoyd.blockworks.mining

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.nbt.PersistentDataUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


/**
 * Class for adding plugin-unique information about an ItemStack.
 */
class MiningTool : BlockworksAttributable {

    val itemStack: ItemStack
    val blockworks : Blockworks

    /**
     * @param item the [ItemStack] representation of the item that players will hold when mining blocks.
     * Should have a count of 1, but do whatever you want IDK
     * @param data the list of attributes that this MiningItem should have, and what the values should be.
     * @param writeData whether the constructor should write the provided data to the underlying [ItemStack]'s
     * [org.bukkit.persistence.PersistentDataContainer]. Recommended for item creation, not recommended if [data]
     * is empty or when preserving existing data values.
     */
    constructor(blockworks: Blockworks, item : ItemStack, data : Map<MiningAttribute<*,*>, Any>, writeData : Boolean = true) {
        this.blockworks = blockworks
        itemStack = item
        if (item.type == Material.AIR || !writeData) return
        val meta = itemStack.itemMeta
        PersistentDataUtil.setTag(blockworks.plugin, meta, "isMiningItem", PersistentDataType.BOOLEAN, true)
        itemStack.itemMeta = meta

        @Suppress("UNCHECKED_CAST")
        data.forEach { (att, data) -> setAttribute<Any, Any>(att as MiningAttribute<Any, Any>, data) }

    }

    override fun <P : Any, C : Any> setAttribute(miningAttribute: MiningAttribute<P, C>, value : C) {
        val meta = itemStack.itemMeta
        PersistentDataUtil.setTag(blockworks.plugin, meta, miningAttribute.name, miningAttribute.persistentDataType, value)
        itemStack.itemMeta = meta
    }

    override fun <P : Any, C : Any> getAttribute(miningAttribute: MiningAttribute<P, C>) : C {
        return PersistentDataUtil.getTag(blockworks.plugin, itemStack.itemMeta, miningAttribute.name, miningAttribute.persistentDataType)
    }


}