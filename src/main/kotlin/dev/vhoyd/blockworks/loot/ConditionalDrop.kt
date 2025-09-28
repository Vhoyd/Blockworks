package dev.vhoyd.blockworks.loot

import dev.vhoyd.blockworks.block.WeightedEntry
import dev.vhoyd.blockworks.mining.MiningPlayer
import org.bukkit.Location
import org.bukkit.inventory.ItemStack

/**
 * Framework for any set of block drops meant to be provided when a block is broken.
 * This allows for a check to happen if the drop(s) should only be rewarded under certain circumstances (hence the name).
 * All exp and drop entries are treated as exclusive and dependent; you cannot drop more than one item type from a given
 * `ConditionalDrop`. If you want more than one drop, simply make more `ConditionalDrop` objects.
 */
class ConditionalDrop {
    val drops : WeightedEntryPool<ItemStack>
    val exp : WeightedEntryPool<Int>
    val condition : (player : MiningPlayer) -> Boolean
    val locationOffset : Location

    /**
     * @param expYield the amount of exp yielded when breaking the block, picked at random, exclusive.
     * @param drops the drops to reward a player with when the block is broken, picked at random, exclusive.
     * @param condition the condition under which this `ConditionalDrop` yields its xp and drops
     * @param locationOffset an amount to shift the drop coordinates by, relative to the block broken. Defaults to `(0,5, 0,5, 0,5)`
     */
    constructor(expYield: List<WeightedEntry<Int>>, drops: List<WeightedEntry<ItemStack>>, locationOffset: Location = Location(null, 0.5, 0.5, 0.5), condition: (player : MiningPlayer) -> Boolean = { true } ) :
            this(WeightedEntryPool(expYield), WeightedEntryPool(drops), locationOffset, condition)


    /**
     * @param expYield the amount of exp yielded when breaking the block
     * @param drop the drop to reward a player with when the block is broken
     * @param condition the condition under which this `ConditionalDrop` yields its xp and drops
     * @param locationOffset an amount to shift the drop coordinates by, relative to the block broken. Defaults to `(0,5, 0,5, 0,5)`
     */
    constructor(expYield: WeightedEntry<Int>, drop: WeightedEntry<ItemStack>, locationOffset: Location = Location(null, 0.5, 0.5, 0.5), condition: (player : MiningPlayer) -> Boolean = { true } ) :
            this(WeightedEntryPool(listOf(expYield)), WeightedEntryPool(listOf(drop)), locationOffset, condition)


    /**
     * @param expYield the amount of exp yielded when breaking the block, picked at random, exclusive.
     * @param drops the drops to reward a player with when the block is broken, picked at random, exclusive.
     * @param condition the condition under which this `ConditionalDrop` yields its xp and drops
     * @param locationOffset an amount to shift the drop coordinates by, relative to the block broken. Defaults to `(0,5, 0,5, 0,5)`
     */
    constructor(expYield: WeightedEntryPool<Int>, drops: WeightedEntryPool<ItemStack>, locationOffset: Location = Location(null, 0.5, 0.5, 0.5),  condition: (player : MiningPlayer) -> Boolean = { true }) {
        this.drops = drops
        exp = expYield
        this.condition = condition
        this.locationOffset = locationOffset
    }

    /**
     * @param expYield the amount of exp yielded when breaking the block
     * @param drop the drop to reward a player with when the block is broken
     * @param condition the condition under which this `ConditionalDrop` yields its xp and drops
     * @param locationOffset an amount to shift the drop coordinates by, relative to the block broken. Defaults to `(0,5, 0,5, 0,5)`
     */
    constructor(expYield: Int, drop: ItemStack, locationOffset: Location = Location(null, 0.5, 0.5, 0.5), condition: (player : MiningPlayer) -> Boolean) :
            this(WeightedEntryUtil.single(expYield), WeightedEntryUtil.single(drop), locationOffset, condition)

    /**
     * @return the list of possible item drops from this `ConditionalDrop`
     */
    fun getDropList(): List<WeightedEntry<ItemStack>> {
        return drops.entries
    }
    /**
     * @return the amount of exp this `ConditionalDrop` should reward the player
     */
    fun getExpList(): List<WeightedEntry<Int>> {
        return exp.entries
    }


}