package dev.vhoyd.blockworks.loot

import dev.vhoyd.blockworks.block.BlockInstance
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

/**
 * Framework for any set of block drops meant to be provided when a block is broken.
 * This allows for a check to happen if the drop(s) should only be rewarded under certain circumstances (hence the name).
 * All exp and drop entries are treated as exclusive and dependent; you cannot drop more than one item type from a given
 * `ConditionalDrop`. If you want more than one drop, simply make more `ConditionalDrop` objects.
 */
data class ConditionalDrop(
    val expPool: WeightedEntryPool<Int>,
    val dropPool: WeightedEntryPool<ItemStack>,
    val locationOffset: Location = Location(null, 0.5, 0.5, 0.5),
    val condition: (BlockInstance) -> Boolean = { true }
)
