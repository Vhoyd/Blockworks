package dev.vhoyd.blockworks.api.event

import dev.vhoyd.blockworks.api.block.BlockInstance
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event class called each tick of a [dev.vhoyd.blockworks.api.model.BlockBreaker] breaking some [BlockInstance].
 */
@Suppress("unused") // for external use only
class BlockInstanceTickEvent internal constructor(
    val block: BlockInstance
) : Event() {
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