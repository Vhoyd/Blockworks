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
