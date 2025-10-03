package dev.vhoyd.blockworks.core

import dev.vhoyd.blockworks.block.BlockDefinition
import dev.vhoyd.blockworks.listener.SpigotEventListener
import dev.vhoyd.blockworks.mining.MiningPlayer
import dev.vhoyd.blockworks.tick.BlockBreakTick
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType


/**
 * Entry point class for working with the API.
 * @param applyAllPlayers whether to immediately apply plugin behavior to all players on the server.
 * If this is false, this plugin will only work if you manually call [registerPlayer].
 */
class Blockworks(val config: Config, val applyAllPlayers : Boolean = true, val applyBehavior : (Player) -> Unit = {applyPotionEffects(it)})  {

    companion object {
        fun applyPotionEffects(player : Player) {
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.MINING_FATIGUE,
                    Int.MAX_VALUE,
                    5,
                    true,
                    false,
                    false
                )
            )
            player.addPotionEffect(
                PotionEffect(
                    PotionEffectType.HASTE,
                    Int.MAX_VALUE,
                    1,
                    true,
                    false,
                    false
                )
            )
        }
    }

    val plugin : Plugin = config.plugin
    val players = mutableSetOf<MiningPlayer>()
    val breakTick = BlockBreakTick(this)

    init {

        val eventHandler = SpigotEventListener(this)
        breakTick.runTaskTimer(plugin, 0, 0)

        plugin.server.pluginManager.registerEvents(eventHandler, plugin)
        if (applyAllPlayers) plugin.server.onlinePlayers.forEach {applyBehavior(it)}
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

    fun registerPlayer(player : Player) = players.add(MiningPlayer(player, this))

    /**
     * @return the [BlockDefinition] that overrides block behavior of the given [Material], or `null``
     * if no behavior is assigned to it.
     */
    fun getBlock(material: Material) : BlockDefinition? {
        val index = config.materialList.indexOf(material)
        if (index == -1) return null
        return config.blockDefinitions[index]
    }

}