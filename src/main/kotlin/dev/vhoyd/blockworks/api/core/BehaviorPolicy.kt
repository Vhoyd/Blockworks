package dev.vhoyd.blockworks.api.core

import com.google.common.base.Predicate
import dev.vhoyd.blockworks.api.block.BlockDefinition
import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.loot.DeterminedDrop
import org.bukkit.Material
import java.util.function.Consumer


/**
 * Data class for setting up a lot of miscellaneous behavior for Blockworks.
 * @property loggingLevel the severity of alerts that the user wants output to server logs.
 */


class BehaviorPolicy @JvmOverloads constructor(
    internal val loggingLevel: LoggingLevel = LoggingLevel.WARN,
    definitionProducer: DefinitionProducer,
    internal val defaultReplacementMaterial : Material = Material.AIR,
    internal val defaultBreakCondition : Predicate<BlockInstance>,
    internal val defaultDropBehavior : Consumer<DeterminedDrop> = BlockDefinition.defaultDropBehavior,
) {

    val definitions = definitionProducer.produceDefinitions(this)

    enum class LoggingLevel(val level : Byte) {
        DEBUG(4),
        INFO(3),
        WARN(2),
        ERROR(1),

        @Suppress("unused") // for external use only
        NONE(0)
    }

    override fun toString(): String {
        return StringBuilder("BehaviorPolicy(")
            .append("logging level: ${loggingLevel.name},\n")
            .append(" block definitions: ")
            .appendIterable(definitions)
            .append(", default replacement material: $defaultReplacementMaterial,\n")
            .toString()
    }
}