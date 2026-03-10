package dev.vhoyd.blockworks.api.loot.entry


class UniformEntryPool<out T>(entries : List<Entry<T>>) : EntryPool<T>(entries) {

    override fun pickRandom(): T {
        return entries.random().data
    }

    fun get(index: Int): T {
        return entries[index].data
    }
}