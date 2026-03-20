package dev.vhoyd.blockworks.api.event

import dev.vhoyd.blockworks.api.block.BlockInstance
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event called when Blockworks internally creates a new [BlockInstance].
 *
 * This will always be created via
 * [dev.vhoyd.blockworks.api.block.BlockDefinition.createInstance].
 */
@Suppress("unused") // for external use only
class BlockInstanceCreateEvent internal constructor(
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