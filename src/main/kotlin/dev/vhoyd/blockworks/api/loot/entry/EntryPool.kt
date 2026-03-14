package dev.vhoyd.blockworks.api.loot.entry

import dev.vhoyd.blockworks.api.core.appendIterable


sealed class EntryPool<out T>(protected val entries : List<Entry<T>>) : Iterable<Entry<T>> {

    abstract fun pickRandom(): T

    override fun iterator(): Iterator<Entry<T>> = entries.iterator()

    override fun toString(): String {
        return StringBuilder(this::class.java.simpleName)
            .appendIterable(entries)
            .toString()
    }


    companion object {
        /**
         * Generates a `List<`[Entry]`>` of size 1. The caller can specify the weight for the created entry.
         */
        @JvmStatic
        fun <V> single(value: V): EntryPool<V> {
            val out = ArrayList<Entry<V>>()
            out.add(Entry(value, 1))
            return UniformEntryPool(out)
        }

        /**
         * Generates a `List<`[Entry]`>` based on a given `Iterable`, such as a `List`, where
         * each entry has the same exact weight. The caller can specify the weight.
         */
        @JvmStatic
        fun <V> uniform(data: Iterable<V>): EntryPool<V> {
            val createdList = ArrayList<Entry<V>>()
            for (entry in data) {
                createdList.add(Entry(entry, 1))
            }
            return UniformEntryPool(createdList)
        }
    }
}