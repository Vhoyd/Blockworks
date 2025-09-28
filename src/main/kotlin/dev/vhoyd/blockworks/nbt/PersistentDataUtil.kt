package dev.vhoyd.blockworks.nbt

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin

/**
 * Utility class for reading from and writing to [org.bukkit.persistence.PersistentDataContainer]s
 */
class PersistentDataUtil {
    private constructor()
    companion object {

        /**
         * writes a tag name and value to an [ItemStack]
          */
        fun <T : Any, Z : Any> setTag(plugin: Plugin, item: ItemStack, tagName: String, type: PersistentDataType<T, Z>, value: Z) {
            val meta = item.itemMeta
            val pdc = meta.persistentDataContainer
            val nsk = NamespacedKey(plugin, tagName)
            pdc.set<T, Z>(nsk, type, value)
            item.setItemMeta(meta)
        }

        /**
         * reads a tag name and value from an [ItemStack]
         */
        fun <T : Any, Z : Any> getTag(plugin: Plugin, item: ItemStack, tagName: String, type: PersistentDataType<T, Z>): Z {
            val meta = item.itemMeta
            val pdc = meta.persistentDataContainer
            val nsk = NamespacedKey(plugin, tagName)
            return pdc.get(nsk, type) as Z
        }
    }
}