package dev.vhoyd.blockworks.block

/**
 * SAM interface for generating a `List` of [BlockDefinition]s for use in tracking
 * block data during [dev.vhoyd.blockworks.core.Config] creation.
 */
fun interface BlockDataLoader {
    fun loadAll(): List<BlockDefinition>
}