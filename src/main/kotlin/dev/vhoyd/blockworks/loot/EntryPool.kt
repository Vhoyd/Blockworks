package dev.vhoyd.blockworks.loot

import dev.vhoyd.blockworks.core.WeightedEntry

abstract class EntryPool<T>(val entries : List<WeightedEntry<T>>) {

    abstract fun pickRandom(): T

    abstract fun pickExact(value : Int) : T
}