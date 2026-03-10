package dev.vhoyd.blockworks.api.loot.entry

data class Entry<out T>(
    val data: T,
    val weight: Int = 1
)