package dev.vhoyd.blockworks.core

import dev.vhoyd.blockworks.block.BlockDefinition
import org.bukkit.Material
import org.bukkit.plugin.Plugin
import kotlin.experimental.or


/**
 * Data class for setting up a lot of miscellaneous behavior for Blockworks.
 * @property plugin the [Plugin] spawning the Blockworks object.
 * @property loggingLevel the severity of alerts that the user wants output to server logs.
 * @property eventMask the types of events
 */
data class Config(
    val plugin : Plugin,
    val loggingLevel: LoggingLevel = LoggingLevel.WARN,
    val eventMask : Byte,
    val blockDefinitions : List<BlockDefinition>,
    val properties: Map<ConfigProperty<*>, Any>,
) {
    val materialList : List<Material> = blockDefinitions.map { it.material }

    companion object {
        fun createEventMask(vararg strategies : EventMaskType) : Byte {
            var mask : Byte = 0
            strategies.forEach { mask = mask.or(it.mask) }
            return mask
        }
    }

    /**
     * @return the value of the specified property.
     * @throws ClassCastException if the property value cannot be cast to the specified type.
     * @throws NullPointerException if the property doesn't exist.
     */
    fun <T> getProperty(property: ConfigProperty<T>) : T {

        @Suppress("UNCHECKED_CAST")
        return properties[property] as T
    }

    operator fun <T> get(property : ConfigProperty<T>) : T = getProperty(property)

    enum class LoggingLevel(val level : Byte) {
        DEBUG(4),
        INFO(3),
        WARN(2),
        ERROR(1),
        NONE(0)
    }

    enum class EventMaskType(val mask : Byte) {
        ALL(15), // 1111
        BLOCK_BREAK_MATCH(8), // 1000
        ITEM_SWITCH(4), // 0100
        BLOCK_DAMAGE(2), // 0010
        BLOCK_BREAK(1), // 0001
        NONE(0); // 0000
    }
}