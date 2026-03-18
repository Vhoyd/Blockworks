package dev.vhoyd.blockworks.api.loot.entry

import kotlin.random.Random

/**
 * Extension of [EntryPool] that provides a weight-based getter.
 */

class WeightedEntryPool<out T>(entries : List<Entry<T>>) : EntryPool<T>(entries) {
    private val totalWeight : Int

    init {
        var sum = 0
        entries.forEach { sum += it.weight }
        totalWeight = sum
    }

    /**
     * Picks a random number from 0 up to the cumulative weight and uses weighted indexing to return the resulting entry.
     */
    override fun pickRandom() : T = pickExact(Random.nextInt(totalWeight))

    /**
     * Returns a specific entry for a specific weight. Since weight is handled based on the order of the
     * underlying `List`, it is necessary to compute the inputted weight based on cumulative weight in
     * ascending index order, 0 -> `list.size`
     */
    fun pickExact(weight : Int) : T {
        var number = weight
        var item : Entry<T> = entries[0]
        val iterator = entries.iterator()
        while (number >= 0 && iterator.hasNext()) {
            item = iterator.next()
            number -= item.weight
        }
        return item.data
    }

}