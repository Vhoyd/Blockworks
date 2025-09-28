package dev.vhoyd.blockworks.mining

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.nbt.PersistentDataUtil
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType


/**
 * Class for adding plugin-unique information about an ItemStack.
 */
class MiningTool {

    val miningSpeed : Int
    val miningFortune : Int
    val breakingPower : Int
    val itemStack: ItemStack

    /**
     * @param item the [ItemStack] representation of the item that players will hold when mining blocks.
     * Should have a count of 1, but do whatever you want IDK
     * @param writeData whether the constructor should write the provided data to the underlying [ItemStack]'s
     * [org.bukkit.persistence.PersistentDataContainer]. This is highly recommended as that is how items are evaluated when players
     * switch held items, but can be skipped to protect existing values under special circumstances.
     */
    constructor(blockworks: Blockworks, speed: Int, fortune: Int, power: Int, item: ItemStack, writeData : Boolean = true) {
        this.miningSpeed = speed
        this.breakingPower = power
        this.miningFortune = fortune
        itemStack = item
        if (item.type == Material.AIR || !writeData) return
        PersistentDataUtil.setTag(blockworks.plugin, itemStack, "isMiningItem", PersistentDataType.BOOLEAN, true)
        PersistentDataUtil.setTag(blockworks.plugin, itemStack, "speed", PersistentDataType.INTEGER, speed)
        PersistentDataUtil.setTag(blockworks.plugin, itemStack, "fortune", PersistentDataType.INTEGER, fortune)
        PersistentDataUtil.setTag(blockworks.plugin, itemStack, "power", PersistentDataType.INTEGER, power)
    }


}