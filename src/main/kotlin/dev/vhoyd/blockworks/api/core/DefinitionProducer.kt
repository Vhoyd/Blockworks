package dev.vhoyd.blockworks.api.core

import dev.vhoyd.blockworks.api.block.BlockDefinition

/**
 * Produces a `List<`[BlockDefinition]`>` given the context of the containing [Config]
 */
fun interface DefinitionProducer {
    fun produceDefinitions(config : Config) : List<BlockDefinition>
}