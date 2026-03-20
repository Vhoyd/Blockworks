package dev.vhoyd.blockworks.api.core

import dev.vhoyd.blockworks.api.block.BlockDefinition

/**
 * Produces an `Iterable<`[BlockDefinition]`>` given the context of the containing [BehaviorPolicy]
 */
fun interface DefinitionProducer {
    fun produceDefinitions(behaviorPolicy : BehaviorPolicy) : Iterable<BlockDefinition>
}