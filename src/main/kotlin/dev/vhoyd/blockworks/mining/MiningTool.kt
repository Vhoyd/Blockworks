package dev.vhoyd.blockworks.mining

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.nbt.PersistentDataUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack


/**
 * Class for adding plugin-unique information about an ItemStack.
 */
class MiningTool(
    val blockworks: Blockworks,
    val item : ItemStack?,
    data : Map<MiningAttribute<*,*>, Any> = emptyMap(),
    writeData : Boolean = true
) : BlockworksAttributable  {

    val itemStack : ItemStack = item ?: ItemStack(Material.AIR)

    /**
     * @param itemStack the [ItemStack] representation of the item that players will hold when mining blocks.
     * Should have a count of 1, but do whatever you want IDK
     * @param data the list of attributes that this MiningItem should have, and what the values should be.
     * @param writeData whether the constructor should write the provided data to the underlying [ItemStack]'s
     * [org.bukkit.persistence.PersistentDataContainer]. Recommended for item creation, not recommended if [data]
     * is empty or when preserving existing data values.
     */
    init {
        if (itemStack.type != Material.AIR && writeData) {
            val meta = itemStack.itemMeta
            @Suppress("UNCHECKED_CAST")
            data.forEach { (key, value) -> setAttribute(key as MiningAttribute<Any, Any>, value) }
            itemStack.itemMeta = meta
        }
    }

    override fun <P : Any, C : Any> setAttribute(miningAttribute: MiningAttribute<P, C>, value : C) {
        val meta = itemStack.itemMeta
        PersistentDataUtil.setTag(blockworks.plugin, meta, "blockworks-${miningAttribute.name}", miningAttribute.persistentDataType, value)
        itemStack.itemMeta = meta
    }

    override fun <P : Any, C : Any> getAttribute(miningAttribute: MiningAttribute<P, C>) : C {
        return PersistentDataUtil.getTag(blockworks.plugin, itemStack.itemMeta, "blockworks-${miningAttribute.name}", miningAttribute.persistentDataType)
    }

}