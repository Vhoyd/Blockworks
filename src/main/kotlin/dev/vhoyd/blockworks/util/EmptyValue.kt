package dev.vhoyd.blockworks.util

import dev.vhoyd.blockworks.core.Config
import dev.vhoyd.blockworks.block.BlockBreakAction
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.loot.ConditionalDrop
import dev.vhoyd.blockworks.block.BlockDefinition
import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.mining.MiningPlayer
import dev.vhoyd.blockworks.loot.WeightedEntryUtil
import dev.vhoyd.blockworks.mining.MiningTool
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin
import org.bukkit.plugin.java.JavaPlugin

/**
 * Utility class for null-safe "null" values of various types.
 */
class EmptyValue {
    private constructor()
    companion object {
        val ITEMSTACK = ItemStack(Material.AIR, 1)
        val BLOCKBREAKACTION : BlockBreakAction = BlockBreakAction { _ : BlockInstance, _ : MiningPlayer -> }
        val CONDITIONALDROP = ConditionalDrop(
            WeightedEntryUtil.single(0), WeightedEntryUtil.single(ItemStack(
            Material.AIR))) { true }
        val BLOCKDEFINITION = BlockDefinition(Material.AIR, CONDITIONALDROP, -1, -1, BLOCKBREAKACTION )
        val CONFIG = Config({ listOf(BLOCKDEFINITION) }, miningSpeedScale = 0.0)
        val BLOCKINSTANCE = BlockInstance(
            BLOCKDEFINITION,
            location = Location(null, -1.0, -1.0, -1.0),
            config = CONFIG
        )
    }
}