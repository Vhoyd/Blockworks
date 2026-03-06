package dev.vhoyd.blockworks.nbt

import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataHolder
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

/**
 * Utility class for reading from and writing to [org.bukkit.persistence.PersistentDataContainer]s
 */
object PersistentDataUtil{

    fun <T : Any, Z : Any> setTag(plugin: Plugin, target : PersistentDataHolder, tagName: String, type: PersistentDataType<T, Z>, value: Z) {
        val pdc = target.persistentDataContainer
        val nsk = NamespacedKey(plugin, tagName)
        pdc.set<T, Z>(nsk, type, value)
    }

    fun <T : Any, Z : Any> getTag(plugin: Plugin, target: PersistentDataHolder, tagName: String, type: PersistentDataType<T, Z>): Z {
        val pdc = target.persistentDataContainer
        val nsk = NamespacedKey(plugin, tagName)
        return pdc.get(nsk, type) as Z
    }
}

