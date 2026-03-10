package dev.vhoyd.blockworks.api.loot

import org.bukkit.inventory.ItemStack
import dev.vhoyd.blockworks.api.block.BlockDefinition
import dev.vhoyd.blockworks.api.block.BlockInstance

/**
 * Data class constructed  after rolling every [ConditionalDrop] in a [BlockDefinition]. Also acts as a pre-deploy wrapper
 * for breaking apart [ItemStack]s with amounts greater than 64. <P>
 * Intended use of this class is potentially modifying the amount of any specific [items] entry (with no 64 upper limit),
 * or [exp] amount, then accessing the 64-split version of `items` through [splitDrops]
 * @property splitDrops Returns a version of [items] where any [ItemStack] with an amount greater than 64 is broken into multiple
 * entries, e.g. a count of 129 -> {64, 64, 1}. Lazy-set; will not change after first call.
 *
 */
data class DeterminedDrop(
    val blockInstance : BlockInstance,
    val items: Set<ItemStack>,
    val exp: Set<Int>
) {

    val splitDrops: Set<ItemStack> by lazy { processItems() }
    private val log = blockInstance.breaker.blockworks.logger.context("DeterminedDrop:${blockInstance.location.block.blockData.material}")

    private fun processItems() : Set<ItemStack> {
        val split = mutableSetOf<ItemStack>()
        log.debug("Splitting list of size ${items.size}")
        items.forEach { drop ->
            while (drop.amount > 64) {
                val clone = drop.clone()
                clone.amount = 64
                split.add(clone)
                drop.amount -= 64
            }
            if (drop.amount > 0) split.add(drop)
        }
        log.debug("List split into size ${split.size}")
        return split
    }

    operator fun plusAssign(amount : Int) = items.forEach { it.amount += amount }
    operator fun minusAssign(amount : Int) = items.forEach { it.amount -= amount }
    operator fun timesAssign(amount : Float) = items.forEach { it.amount = (it.amount * amount).toInt() }
    operator fun timesAssign(amount : Int) = items.forEach { it.amount *= amount }
    operator fun divAssign(amount : Float) = items.forEach { (it.amount / amount).toInt() }
    operator fun divAssign(amount : Int) = items.forEach { it.amount /= amount }
    operator fun remAssign(amount : Int) = items.forEach { it.amount %= amount }

    /**
     * Increases the amount of every [ItemStack] in [items] by the given `amount`. Supports `+=` operator.
     */
    fun addAll(amount : Int) = plusAssign(amount)

    /**
     * Decreases the amount of every [ItemStack] in [items] by the given `amount`. Supports `-=` operator.
     */
    fun subtractAll(amount : Int) = minusAssign(amount)

    /**
     * Multiplies the amount of every [ItemStack] in [items] by the given `amount`. Supports `*=` operator.
     */
    fun multiplyAll(amount : Float) = timesAssign(amount)
    /**
     * Multiplies the amount of every [ItemStack] in [items] by the given `amount`. Supports `*=` operator.
     */
    fun multiplyAll(amount : Int) = timesAssign(amount)

    /**
     * Divides the amount of every [ItemStack] in [items] by the given `amount`. Supports `/=` operator.
     */
    fun divideAll(amount : Float) = divAssign(amount)

    /**
     * Integer-divides the amount of every [ItemStack] in [items] by the given `amount`. Supports `/=` operator.
     */
    fun divideAll(amount : Int) = divAssign(amount)
}