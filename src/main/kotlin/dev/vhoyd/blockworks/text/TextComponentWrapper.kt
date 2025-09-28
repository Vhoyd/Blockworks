package dev.vhoyd.blockworks.text

import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemStack

/**
 * Utility class for turning strings into [net.kyori.adventure.text.TextComponent]s faster.
 * Currently only for [ItemStack] usage.
 */
class TextComponentWrapper {
    private constructor()
    companion object {

        /**
         * Modifies a given [ItemStack] to inherit the given display name and lore.
         * Each entry in the lore argument will be treated as a new line; color is left to the caller
         * via the section sign symbol ('§').
         */
        fun createTextedItem(item: ItemStack, displayName : String, lore : List<String>) : ItemStack {
            val meta = item.itemMeta
            val componentLore = ArrayList<Component>()
            for (i in 0..<lore.size) {
                componentLore.add(Component.text(lore[i] + "\n"))
            }
            componentLore.add(Component.text(lore[lore.size-1]))
            meta.lore(componentLore)
            meta.displayName(Component.text(displayName))
            item.itemMeta = meta
            return item
        }

        /**
         * Modifies a given [ItemStack] to inherit the given display name and lore. color is left to the caller
         * via the section sign symbol ('§').
         */
        fun createTextedItem(item: ItemStack, displayName : String, lore : String) : ItemStack {
            val meta = item.itemMeta
            val componentLore = ArrayList<Component>()
            componentLore.add(Component.text(lore))
            meta.lore(componentLore)
            meta.displayName(Component.text(displayName))
            item.itemMeta = meta
            return item
        }
    }
}