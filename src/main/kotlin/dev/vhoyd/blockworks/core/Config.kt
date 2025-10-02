package dev.vhoyd.blockworks.core

import dev.vhoyd.blockworks.block.BlockDefinition
import org.bukkit.Material
import org.bukkit.plugin.Plugin

typealias ConfigProperty<T> = Pair<String, Class<T>>

open class Config {
    val plugin : Plugin
    val blockList : List<BlockDefinition>
    val materialList : List<Material>
    val properties : Map<ConfigProperty<*>, *>


    /**
     * @param blockDefinitionList a `List<`[BlockDefinition]`>` describing how certain blocks should behave
     * under this config.
     * @param properties a `Map<`[ConfigProperty]`, Any>` that documents what properties this config should have.
     */

    constructor(
        plugin : Plugin,
        blockDefinitionList : List<BlockDefinition>,
        properties: Map<ConfigProperty<*>, Any>
    ) {
        this.plugin = plugin
        blockList = blockDefinitionList
        materialList = blockList.map { it.material }
        this.properties = properties

    }

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