package dev.vhoyd.blockworks.util

import dev.vhoyd.blockworks.block.BlockBreakAction
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.loot.ConditionalDrop
import dev.vhoyd.blockworks.block.BlockDefinition
import dev.vhoyd.blockworks.mining.MiningPlayer
import dev.vhoyd.blockworks.loot.WeightedEntryUtil
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

/**
 * Utility class for `null`-safe "null" values of various types.
 */
class EmptyValue {
    private constructor()
    companion object {
        val BLOCKBREAKACTION : BlockBreakAction = BlockBreakAction { _ : BlockInstance, _ : MiningPlayer -> }
        val CONDITIONALDROP = ConditionalDrop(
            WeightedEntryUtil.single(0), WeightedEntryUtil.single(ItemStack(
            Material.AIR))) { true }
        val BLOCKDEFINITION = BlockDefinition(Material.AIR, CONDITIONALDROP, breakCondition =  { false } )
        val BLOCKINSTANCE = BlockInstance(
            BLOCKDEFINITION,
            location = Location(null, -1.0, -1.0, -1.0),
            TODO())
    }
    
}