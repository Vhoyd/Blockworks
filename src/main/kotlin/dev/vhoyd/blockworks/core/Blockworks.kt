package dev.vhoyd.blockworks.core

import dev.vhoyd.blockworks.block.BlockDefinition
import dev.vhoyd.blockworks.listener.SpigotEventListener
import dev.vhoyd.blockworks.mining.MiningPlayer
import dev.vhoyd.blockworks.mining.MiningTool
import dev.vhoyd.blockworks.nbt.PersistentDataUtil
import dev.vhoyd.blockworks.tick.BlockBreakTick
import dev.vhoyd.blockworks.util.EmptyValue
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.Plugin


/**
 * Entry point class for working with the API.
 */
class Blockworks {
    val plugin : Plugin
    val players = ArrayList<MiningPlayer>()
    val config : Config

    constructor(config : Config) {
        this.plugin = config.plugin
        this.config = config

        val breakTick = BlockBreakTick(this)
        val eventHandler = SpigotEventListener(this)
        breakTick.runTaskTimer(plugin, 0, 0)

        plugin.server.pluginManager.registerEvents(eventHandler, plugin)
        plugin.logger.info("Blockworks (via ${plugin.name}) is running!")

    }

    /**
     * @return the corresponding [MiningPlayer] object for a given [Player], or `null` if none exists.
     */
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
     * Checks for an existing [MiningTool] represented by the given [ItemStack]
     * @param item the [ItemStack] to filter by
     * @return the matching [MiningTool], or `null` if no mining data is found for the [ItemStack]
     */
    fun evaluateItem(item: ItemStack?): MiningTool? {
        if (item == null) return null
        try {
            if (PersistentDataUtil.getTag(plugin, item.itemMeta, "isMiningItem", PersistentDataType.BOOLEAN)) {
                return MiningTool(
                    this,
                    item,
                    emptyMap(),
                    false
                )
            }
        } catch (_ : NullPointerException) {
            return null
        }

        return null
    }


    /**
     * @return the [BlockDefinition] that overrides block behavior of the given [Material], or [EmptyValue.BLOCKDEFINITION]
     * if no behavior is assigned to it.
     */
    fun getBlock(material: Material) : BlockDefinition {
        val index = config.materialList.indexOf(material)
        if (index == -1) return EmptyValue.BLOCKDEFINITION
        return config.blockList[index]
    }






}