package dev.vhoyd.blockworks.api.core

import com.google.common.base.Predicate
import dev.vhoyd.blockworks.api.block.BlockDefinition
import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.loot.DeterminedDrop
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import java.util.function.Consumer
import kotlin.experimental.or


/**
 * Data class for setting up a lot of miscellaneous behavior for Blockworks.
 * @property plugin the [Plugin] spawning the Blockworks object.
 * @property loggingLevel the severity of alerts that the user wants output to server logs.
 * @property eventMask the types of events to ignore handling. See [createEventMask]
 */


class Config @JvmOverloads constructor(
    internal val plugin : Plugin,
    internal val loggingLevel: LoggingLevel = LoggingLevel.WARN,
    internal val eventMask : Byte,
    definitionProducer: DefinitionProducer,
    internal val defaultReplacementMaterial : Material = Material.AIR,
    internal val defaultBreakCondition : Predicate<BlockInstance>,
    internal val defaultDropBehavior : Consumer<DeterminedDrop> = BlockDefinition.defaultDropBehavior,
) {

    val definitions = definitionProducer.produceDefinitions(this)

    companion object {
        /**
         * @return a Byte based on the provided [EventMaskType]s
         */
        @JvmStatic
        fun createEventMask(vararg masks : EventMaskType) : Byte {
            var mask : Byte = 0
            masks.forEach { mask = mask.or(it.mask) }
            return mask
        }
    }


    enum class LoggingLevel(val level : Byte) {
        DEBUG(4),
        INFO(3),
        WARN(2),
        ERROR(1),
        @Suppress("unused") // for external use only
        NONE(0)
    }

    enum class EventMaskType(internal val mask : Byte) {
        @Suppress("unused") // for external use only
        ALL(7), // 111
        BLOCK_BREAK_MATCH(4), // 100
        BLOCK_DAMAGE(2), // 010
        BLOCK_BREAK(1), // 001
        @Suppress("unused") // for external use only
        NONE(0); // 000
    }

    override fun toString(): String {
        return StringBuilder("Config(")
            .append("plugin: ${plugin.name},\n")
            .append("logging level: ${loggingLevel.name},\n")
            .append("event mask byte: $eventMask,\n")
            .append(" block definitions: ")
            .appendIterable(definitions)
            .append(", default replacement material: $defaultReplacementMaterial,\n")
            .toString()
    }
}