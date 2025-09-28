package dev.vhoyd.blockworks.loot

import dev.vhoyd.blockworks.block.WeightedEntry

/**
 * Utility class for quickly generating [WeightedEntry]s, or `List`s of them.
 */
class WeightedEntryUtil{
    private constructor()
    companion object {

        /**
         * Generates a `List` containing exactly 1 [WeightedEntry]. The caller can specify the weight.
         */
        fun <V> single(value : V, weight : Int = 1) : List<WeightedEntry<V>> {
            val out = ArrayList<WeightedEntry<V>>()
            out.add(WeightedEntry(value, weight))
            return out
        }

        /**
         * Generates a `List` of [WeightedEntry]s based on a given `Iterable`, such as a `List`, where
         * each entry has the same exact weight. The caller can specify the weight.
         */
        fun <V> uniformWeight(data : Iterable<V>, weight : Int = 1) : List<WeightedEntry<V>> {
            val createdList = ArrayList<WeightedEntry<V>>()
            for (entry in data) {
                createdList.add(WeightedEntry(entry, weight))
            }
            return createdList
        }
    }

}