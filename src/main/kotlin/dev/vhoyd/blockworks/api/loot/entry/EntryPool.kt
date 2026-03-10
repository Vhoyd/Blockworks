package dev.vhoyd.blockworks.api.loot.entry



sealed class EntryPool<out T>(protected val entries : List<Entry<T>>) {

    abstract fun pickRandom(): T


    companion object {
        /**
         * Generates a `List<`[Entry]`>` of size 1. The caller can specify the weight for the created entry.
         */
        fun <V> single(value: V): EntryPool<V> {
            val out = ArrayList<Entry<V>>()
            out.add(Entry(value, 1))
            return UniformEntryPool(out)
        }

        /**
         * Generates a `List<`[Entry]`>` based on a given `Iterable`, such as a `List`, where
         * each entry has the same exact weight. The caller can specify the weight.
         */
        fun <V> uniform(data: Iterable<V>): EntryPool<V> {
            val createdList = ArrayList<Entry<V>>()
            for (entry in data) {
                createdList.add(Entry(entry, 1))
            }
            return UniformEntryPool(createdList)
        }
    }
}