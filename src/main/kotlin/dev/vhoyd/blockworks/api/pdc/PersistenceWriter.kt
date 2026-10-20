package dev.vhoyd.blockworks.api.pdc

import dev.vhoyd.blockworks.api.model.Attribute
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

/**
 * Utility class for reading from and writing to [org.bukkit.persistence.PersistentDataContainer]s
 */
object PersistenceWriter {

    @JvmStatic
    fun <T : Any, Z : Any> setValue(
        plugin: Plugin,
        target: PersistentDataHolder,
        attribute: Attribute<T, Z>,
        value: Z
    ) {
        val pdc = target.persistentDataContainer
        val nsk = NamespacedKey(plugin, attribute.name)
        pdc.set<T, Z>(nsk, attribute.type, value)
    }

    @JvmStatic
    fun <T : Any, Z : Any> getValue(
        plugin: Plugin,
        target: PersistentDataHolder,
        attribute: Attribute<T, Z>
    ): Z? {
        val pdc = target.persistentDataContainer
        val nsk = NamespacedKey(plugin, attribute.name)
        return pdc.get(nsk, attribute.type)
    }
}