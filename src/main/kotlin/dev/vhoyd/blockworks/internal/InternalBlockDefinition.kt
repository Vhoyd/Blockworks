package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.block.BlockDefinition
import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.core.appendIterable
import dev.vhoyd.blockworks.api.core.appendMap
import dev.vhoyd.blockworks.api.loot.ConditionalDrop
import dev.vhoyd.blockworks.api.loot.DeterminedDrop
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.BlockBreaker
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.function.Predicate

internal class InternalBLockDefinition(
    override val requirements: BiPredicate<Block, BlockBreaker<*>>,
    override val drops: Iterable<ConditionalDrop>,
    override val attributes: MutableMap<Attribute<*, *>, Any>,
    override val breakIf: Predicate<BlockInstance>,
    override val replacement: Material,
    override val onTick: Consumer<BlockInstance>,
    override val onBreak: Consumer<BlockInstance>,
    override val onDrop: Consumer<DeterminedDrop>,
) : BlockDefinition {


    /**
     * Definitions cannot have their attributes modified. This should never be called.
     * @throws IllegalStateException why did you call it??
     */
    @Deprecated("Definitions cannot have their attributes modified. This should never be called.")
    override fun <P : Any, C : Any> setAttribute(
        attribute: Attribute<P, C>,
        value: C
    ) {
        error("Block definition attributes represent defaults and cannot be modified after creation.")
    }

    override fun isValidInstance(
        block: Block,
        breaker: BlockBreaker<*>
    ): Boolean = requirements.test(block, breaker)

    override fun createInstance(
        block: Block,
        breaker: BlockBreaker<*>
    ): BlockInstance = InternalBlockInstance(this, block.location, breaker)


    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C? {

        @Suppress("UNCHECKED_CAST")
        return attributes[attribute] as? C
    }


    override fun toString(): String {
        return StringBuilder("InternalBlockDefinition(drops: ")
            .appendIterable(drops)
            .append(", attributes: ")
            .appendMap(attributes)
            .append(", replaced with: ${replacement.name}")
            .append(")")
            .toString()
    }


}
