package dev.vhoyd.blockworks.simple

import dev.vhoyd.blockworks.block.BlockDefinition
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.loot.ConditionalDrop
import dev.vhoyd.blockworks.loot.WeightedEntryUtil
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

class SimpleBlockDefinitionListCreator {
    companion object {
        fun get() : List<BlockDefinition> {


            val drop = ConditionalDrop(
                WeightedEntryUtil.uniformWeight(2..5),
                WeightedEntryUtil.single(ItemStack(Material.BUDDING_AMETHYST, 1))
            ) { it.heldItem?.itemStack?.enchantments?.contains(Enchantment.SILK_TOUCH) ?: false}

            val entry = BlockDefinition(
                Material.BUDDING_AMETHYST,
                listOf(drop),
                attributes = mapOf(SimpleMiningAttribute.BLOCK_STRENGTH to 100),
                breakCondition = { it : BlockInstance ->
                    val dmg = it[SimpleMiningAttribute.BLOCK_DAMAGE].toFloat()
                    val str = it[SimpleMiningAttribute.BLOCK_STRENGTH].toFloat()
                    dmg/str >= 1

                }
            )
            return listOf(entry)
        }
    }
}