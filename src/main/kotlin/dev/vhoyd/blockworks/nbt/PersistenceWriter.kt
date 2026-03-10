package dev.vhoyd.blockworks.nbt

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

/**
 * Utility class for reading from and writing to [org.bukkit.persistence.PersistentDataContainer]s
 */
object PersistenceWriter{

    /**
     *
     */
    @JvmStatic
    fun <T : Any, Z : Any> setValue(plugin: Plugin, target : PersistentDataHolder, key: String, type: PersistentDataType<T, Z>, value: Z) {
        val pdc = target.persistentDataContainer
        val nsk = NamespacedKey(plugin, key)
        pdc.set<T, Z>(nsk, type, value)
    }

    @JvmStatic
    fun <T : Any, Z : Any> getValue(plugin: Plugin, target: PersistentDataHolder, key: String, type: PersistentDataType<T, Z>): Z? {
        val pdc = target.persistentDataContainer
        val nsk = NamespacedKey(plugin, key)
        return pdc.get(nsk, type)
    }
}

