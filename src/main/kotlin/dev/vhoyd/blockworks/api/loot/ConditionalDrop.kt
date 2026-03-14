package dev.vhoyd.blockworks.api.loot

import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.loot.entry.EntryPool
import org.bukkit.inventory.ItemStack
import java.util.function.Predicate

/**
 * Data class meant to be a framework  for any set of block drops meant to be provided when a block is broken.
 * This allows for a check to happen if the drop(s) should only be rewarded under certain circumstances (hence the name).
 * All exp and drop entries are treated as exclusive and dependent; you cannot drop more than one item type from a given
 * `ConditionalDrop`. If you want more than one drop, simply make more `ConditionalDrop` objects.
 */
data class ConditionalDrop @JvmOverloads constructor(
    val exp: EntryPool<Int>,
    val drops: EntryPool<ItemStack>,
    val condition: Predicate<BlockInstance> = Predicate { true },

    ) {

    override fun toString(): String {
        return StringBuilder("ConditionalDrop(")
            .append("exp: ")
            .append(exp)
            .append(", items: ")
            .append(drops)
            .toString()
    }
}
