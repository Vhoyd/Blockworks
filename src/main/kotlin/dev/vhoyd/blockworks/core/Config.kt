package dev.vhoyd.blockworks.core

import com.google.common.base.Predicate
import dev.vhoyd.blockworks.block.BlockDefinition
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.loot.DeterminedDrop
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


data class Config @JvmOverloads constructor(
    internal val plugin : Plugin,
    internal val loggingLevel: LoggingLevel = LoggingLevel.WARN,
    internal val eventMask : Byte,
    internal val blockDefinitions : List<BlockDefinition>,
    internal val defaultReplacementMaterial : Material = Material.AIR,
    internal val defaultBreakCondition : Predicate<BlockInstance>,
    internal val defaultDropBehavior : Consumer<DeterminedDrop> = BlockDefinition.DEFAULT_DROP_BEHAVIOR,
) {

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
        NONE(0)
    }

    enum class EventMaskType(internal val mask : Byte) {
        ALL(7), // 111
        BLOCK_BREAK_MATCH(4), // 100
        BLOCK_DAMAGE(2), // 010
        BLOCK_BREAK(1), // 001
        NONE(0); // 000
    }
}