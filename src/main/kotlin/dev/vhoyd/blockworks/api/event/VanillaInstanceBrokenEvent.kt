package dev.vhoyd.blockworks.api.event

import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.model.BlockBreaker
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Called when a `Player` breaks a "vanilla" [BlockInstance]
 */
@Suppress("unused") // for external use only
class VanillaInstanceBrokenEvent(val block: BlockInstance, val breaker: BlockBreaker<Player>) : Event() {

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