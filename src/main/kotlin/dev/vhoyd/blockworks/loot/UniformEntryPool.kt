package dev.vhoyd.blockworks.loot


class UniformEntryPool<T>(entries : List<WeightedEntry<T>>) : EntryPool<T>(entries) {
    override fun pickRandom(): T {
        return entries.random().first
    }

    override fun pickExact(value: Int): T {
        return entries[value].first
    }
}