package dev.vhoyd.blockworks.event

import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.mining.MiningPlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event class for when a player stops breaking some [BlockInstance]
 */
class BlockInstanceBreakAbortEvent : Event {
    var isCancelled : Boolean = false
    private val handlers : HandlerList = HandlerList()
    val blockInstance : BlockInstance
    val miningPlayer : MiningPlayer

    override fun getHandlers(): HandlerList {
        return handlers
    }

    constructor(blockInstance: BlockInstance, miningPlayer: MiningPlayer) {
        this.blockInstance = blockInstance
        this.miningPlayer = miningPlayer
    }

}