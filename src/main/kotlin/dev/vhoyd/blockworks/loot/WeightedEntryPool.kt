package dev.vhoyd.blockworks.loot

import dev.vhoyd.blockworks.block.WeightedEntry
import kotlin.random.Random

/**
 * This class takes a `List<`[WeightedEntry]`>` and attaches a few extra methods for getting random/specific
 * contents out of that list.
 * When instantiated, an object of this class computes the cumulative weight of all entries and stores it for
 * later use with fetching entries.
 */
class WeightedEntryPool<T> {
    val entries : List<WeightedEntry<T>>
    private val totalWeight : Int

    constructor(entries : List<WeightedEntry<T>>) {
        this.entries = entries
        var sum = 0
        for (entry in entries) {
            sum += entry.second
        }
        totalWeight = sum
    }

    /**
     * Picks a random number from 0 up to the cumulative weight  and uses weighted indexing to return the resulting entry.
     */
    fun pickRandom(): T {
        var randomNumber = Random.nextInt(totalWeight)
        var item : WeightedEntry<T> = entries[0]
        val iterator = entries.iterator()
        while (randomNumber >= 0) {
            item = iterator.next()
            randomNumber -= item.second
        }
        return item.first
    }

    /**
     * Returns a specific entry for a specific weight. Since weight is handled based on the order of the
     * underlying `List`, it is necessary to compute the inputted weight based on cumulative weight in
     * ascending index order, 0 -> `list.size`
     */
    fun pickExact(value : Int) : T {
        var number = value
        var item : WeightedEntry<T> = entries[0]
        val iterator = entries.iterator()
        while (number >= 0) {
            item = iterator.next()
            number -= item.second
        }
        return item.first
    }

}