package dev.vhoyd.blockworks.core

import dev.vhoyd.blockworks.block.BlockInstance

typealias BlockBreakAction = (BlockInstance) -> Unit
typealias ConfigProperty<T> = Pair<String, Class<T>>
typealias WeightedEntry<T> = Pair<T, Int>