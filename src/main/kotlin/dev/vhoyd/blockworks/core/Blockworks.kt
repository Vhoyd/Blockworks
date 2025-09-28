package dev.vhoyd.blockworks.core

import dev.vhoyd.blockworks.block.BlockDefinition
import dev.vhoyd.blockworks.internal.event.MiningEventHandler
import dev.vhoyd.blockworks.mining.MiningPlayer
import dev.vhoyd.blockworks.mining.MiningTool
import dev.vhoyd.blockworks.nbt.PersistentDataUtil
import dev.vhoyd.blockworks.internal.task.BlockBreakTick
import dev.vhoyd.blockworks.util.EmptyValue
import dev.vhoyd.blockworks.text.TextComponentWrapper
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin


/**
 * Entry point class for working with the library.
 */
class Blockworks {
    val plugin : Plugin
    val players = ArrayList<MiningPlayer>()
    val config : Config
    val testStick: MiningTool

    constructor(plugin: Plugin, config : Config) {
        this.plugin = plugin
        this.config = config
        val stickItem = TextComponentWrapper.createTextedItem(ItemStack(Material.STICK, 1), "§6§lGod Stick", "§d§oFor testing purposes only)")

        testStick = MiningTool(this, 25000000, 500, 10, stickItem)

        val breakTick = BlockBreakTick(this)
        val eventHandler = MiningEventHandler(this)
        breakTick.runTaskTimer(plugin, 0, 0)

        plugin.server.pluginManager.registerEvents(eventHandler, plugin)
        val pluginMessage = Component.text("Mining plugin is running!")
        pluginMessage.color(TextColor.color(0, 1, 0))
        plugin.server.consoleSender.sendMessage(pluginMessage)

    }

    fun getMiningPlayer(minecraftPlayer : Player) : MiningPlayer? {
        for (m in players) {
            if (m.minecraftPlayer.uniqueId == minecraftPlayer.uniqueId) {
                return m
            }
        }
        return null
    }

    fun registerPlayer(miningPlayer: MiningPlayer) {
        players.add(miningPlayer)
    }



    /**
     * Checks for an existing MiningTool represented by the given [ItemStack]
     * @param item the [ItemStack] to filter by
     * @return the matching [MiningTool], or null if no mining data is found for the ItemStack
     */
    fun evaluateItem(item: ItemStack?): MiningTool? {
        if (item == null) return null
        try {
            if (PersistentDataUtil.getTag(plugin, item, "isMiningItem", PersistentDataType.BOOLEAN)) {
                return MiningTool(
                    this,
                    PersistentDataUtil.getTag(plugin, item, "speed", PersistentDataType.INTEGER),
                    PersistentDataUtil.getTag(plugin, item, "fortune", PersistentDataType.INTEGER),
                    PersistentDataUtil.getTag(plugin, item, "power", PersistentDataType.INTEGER),
                    item,
                    false
                )
            }
        } catch (_ : NullPointerException) {
            return null
        }

        return null
    }

    fun getBlock(material: Material) : BlockDefinition {
        val index = config.materialList.indexOf(material)
        if (index == -1) return EmptyValue.BLOCKDEFINITION
        return config.blockList[index]
    }






}