package dev.vhoyd.blockworks.listener

import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.event.BlockInstanceBreakAbortEvent
import dev.vhoyd.blockworks.event.BlockInstanceStartBreakEvent
import dev.vhoyd.blockworks.mining.MiningPlayer
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDamageAbortEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.event.player.PlayerItemHeldEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Internal API class for handling most of the server -> plugin interactions. Should not be tinkered with under
 * most circumstances; not inheritable.
 */
class SpigotEventListener : Listener {
    private val blockworks : Blockworks

    constructor(blockworks : Blockworks) {
        this.blockworks = blockworks
    }

    /**
     * Ensures a MiningPlayer object exists for any joining Player entity, and applies two potion effects:
     * Mining fatigue, so player clients do not prematurely break blocks;
     * haste, so players do not see a jarringly slow arm-swing animation.
     */
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        e.getPlayer().addPotionEffect(
            PotionEffect(
                PotionEffectType.MINING_FATIGUE,
                Int.MAX_VALUE,
                5,
                true,
                false,
                false
            )
        )
        e.getPlayer().addPotionEffect(
            PotionEffect(
                PotionEffectType.HASTE,
                Int.MAX_VALUE,
                1,
                true,
                false,
                false
            )
        )


        //create new MiningPlayer object to handle unregistered players
        if (blockworks.getMiningPlayer(e.getPlayer()) == null) {
            blockworks.registerPlayer(MiningPlayer(e.player, blockworks))

        }
    }

    //update mining stats to reflect new item when switching items
    /**
     * Updates tracked breaking tool for the Player's MiningPlayer representation to ensure proper block breaking behavior.
     */
    @EventHandler
    fun onPlayerSwitchItem(e: PlayerItemHeldEvent) {
        val item = e.getPlayer().inventory.getItem(e.newSlot)
        val miningTool  = blockworks.evaluateItem(item) //defaults to hand stats if no custom item is found
        val mp: MiningPlayer? = blockworks.getMiningPlayer(e.getPlayer())
        mp?.heldItem = miningTool
    }


    @EventHandler
    fun onBlockHit(e : BlockDamageEvent) {
        val miningPlayer = blockworks.getMiningPlayer(e.player) ?: return
        val blockDefinition = blockworks.getBlock(e.block.type)
        val blockInstance = BlockInstance(blockDefinition, e.block.location, miningPlayer)
        e.player.server.pluginManager.callEvent(BlockInstanceStartBreakEvent(blockInstance,miningPlayer))

    }

    @EventHandler
    fun onPlayerStopHittingBlock(e : BlockDamageAbortEvent) {
        val miningPlayer = blockworks.getMiningPlayer(e.player) ?: return
        val blockDefinition = blockworks.getBlock(e.block.type)
        val blockInstance = BlockInstance(blockDefinition, e.block.location, miningPlayer)
        e.player.server.pluginManager.callEvent(BlockInstanceBreakAbortEvent(blockInstance,miningPlayer))
    }

}