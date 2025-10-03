package dev.vhoyd.blockworks.core

import dev.vhoyd.blockworks.block.BlockDefinition
import org.bukkit.Material
import org.bukkit.plugin.Plugin

data class Config(
    val plugin : Plugin,
    val blockDefinitions : List<BlockDefinition>,
    val properties: Map<ConfigProperty<*>, Any>
) {
    val materialList : List<Material> = blockDefinitions.map { it.material }

    /**
     * @return the value of the specified property.
     * @throws ClassCastException if the property value cannot be cast to the specified type.
     * @throws NullPointerException if the property doesn't exist.
     */
    fun <T> getProperty(property: ConfigProperty<T>) : T {

        @Suppress("UNCHECKED_CAST")
        return properties[property]!! as T
    }

    operator fun <T> get(property : ConfigProperty<T>) : T = getProperty(property)


}