package dev.vhoyd.blockworks.event

import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.mining.MiningPlayer
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event class called each tick of a player breaking some [BlockInstance]
 */
class BlockInstanceTickEvent(val blockInstance: BlockInstance, val miningPlayer: MiningPlayer) : Event() {
    var cancelled : Boolean = false
    private val handlers : HandlerList = HandlerList()

    override fun getHandlers(): HandlerList {
        return handlers
    }

}