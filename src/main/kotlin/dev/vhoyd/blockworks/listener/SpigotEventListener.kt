package dev.vhoyd.blockworks.listener

import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.event.BlockInstanceBreakAbortEvent
import dev.vhoyd.blockworks.event.BlockInstanceStartBreakEvent
import dev.vhoyd.blockworks.mining.MiningPlayer
import dev.vhoyd.blockworks.mining.MiningTool
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
class SpigotEventListener(private val blockworks : Blockworks) : Listener {

    /**
     * Ensures a MiningPlayer object exists for any joining Player entity, and applies two potion effects:
     * Mining fatigue, so player clients do not prematurely break blocks;
     * haste, so players do not see a jarringly slow arm-swing animation.
     */
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        if (blockworks.applyAllPlayers)
        blockworks.applyBehavior(e.player)
    }

    //update mining stats to reflect new item when switching items
    /**
     * Updates tracked breaking tool for the Player's MiningPlayer representation to ensure proper block breaking behavior.
     */
    @EventHandler
    fun onPlayerSwitchItem(e: PlayerItemHeldEvent) {
        val item = e.getPlayer().inventory.getItem(e.newSlot)
        val miningTool  = MiningTool(blockworks, item) //defaults to hand stats if no custom item is found
        val mp: MiningPlayer? = blockworks.getMiningPlayer(e.getPlayer())
        mp?.heldTool = miningTool
    }


    @EventHandler
    fun onBlockHit(e : BlockDamageEvent) {
        val miningPlayer = blockworks.getMiningPlayer(e.player) ?: return
        val blockDefinition = blockworks.getBlock(e.block.type) ?: return
        val blockInstance = BlockInstance(blockDefinition, e.block.location, miningPlayer)
        e.player.server.pluginManager.callEvent(BlockInstanceStartBreakEvent(blockInstance,miningPlayer))
        blockworks.breakTick.subscribe(blockInstance)
    }

    @EventHandler
    fun onPlayerStopHittingBlock(e : BlockDamageAbortEvent) {
        val miningPlayer = blockworks.getMiningPlayer(e.player) ?: return
        val blockDefinition = blockworks.getBlock(e.block.type) ?: return
        val blockInstance = BlockInstance(blockDefinition, e.block.location, miningPlayer)
        e.player.server.pluginManager.callEvent(BlockInstanceBreakAbortEvent(blockInstance,miningPlayer))
        blockworks.breakTick.unsubscribe(blockInstance)
    }

}