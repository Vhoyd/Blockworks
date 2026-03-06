package dev.vhoyd.blockworks.loot

typealias WeightedEntry<T> = Pair<T, Int>

abstract class EntryPool<T>(val entries : List<WeightedEntry<T>>) {

    abstract fun pickRandom(): T

    abstract fun pickExact(value : Int) : T
}