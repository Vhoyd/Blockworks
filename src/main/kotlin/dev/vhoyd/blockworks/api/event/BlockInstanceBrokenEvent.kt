package dev.vhoyd.blockworks.api.event

import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.loot.DeterminedDrop
import org.bukkit.Material
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event class for when a [dev.vhoyd.blockworks.api.model.BlockBreaker] breaks some [BlockInstance].
 * This does not include "vanilla" instances.
 * @property replacement the `Material` that the in-world `Block` will change to after this event.
 * At construction, this is assigned to `block.definition.replacement`.
 * @see VanillaInstanceBrokenEvent
 */
@Suppress("unused") // for external use only
class BlockInstanceBrokenEvent internal constructor(
    val drops : DeterminedDrop,
    val block: BlockInstance,
    var replacement: Material
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


