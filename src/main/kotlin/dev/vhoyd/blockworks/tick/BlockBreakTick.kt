package dev.vhoyd.blockworks.tick

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.core.Config
import dev.vhoyd.blockworks.event.BlockInstanceBreakEvent
import dev.vhoyd.blockworks.event.BlockInstanceTickEvent
import dev.vhoyd.blockworks.mining.MiningPlayer
import dev.vhoyd.blockworks.util.EmptyValue
import org.bukkit.scheduler.BukkitRunnable

/**
 * Internal API class for updating blocks being broken by players each tick, as Minecraft does not natively
 * trigger an event each tick for this. Cannot be extended; do not tamper with.
 */
class BlockBreakTick : BukkitRunnable {
    val blockworks : Blockworks
    val config : Config

    constructor(blockworks : Blockworks) {
        this.blockworks = blockworks
        config = blockworks.config
    }

    override fun run() {
        val eventTarget = blockworks.plugin.server.pluginManager
        for (player : MiningPlayer in blockworks.players) {
            val currentBlock = player.currentBlock
            if (currentBlock != EmptyValue.BLOCKINSTANCE) {
                eventTarget.callEvent(BlockInstanceTickEvent(currentBlock, player))
                if (currentBlock.isBroken()) eventTarget.callEvent(BlockInstanceBreakEvent(currentBlock, player))
            }
        }
    }
}