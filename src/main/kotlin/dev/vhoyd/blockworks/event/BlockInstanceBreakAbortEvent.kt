package dev.vhoyd.blockworks.event

import dev.vhoyd.blockworks.block.BlockInstance
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event class for when a player stops breaking some [BlockInstance]
 */
class BlockInstanceBreakAbortEvent(val target: BlockInstance) : Event() {

    var isCancelled : Boolean = false


    companion object {
        private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList() : HandlerList {
            return handlerList
        }
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

}