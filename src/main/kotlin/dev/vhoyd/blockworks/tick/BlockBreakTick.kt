package dev.vhoyd.blockworks.tick

import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.event.BlockInstanceBreakEvent
import dev.vhoyd.blockworks.event.BlockInstanceTickEvent
import dev.vhoyd.blockworks.mining.MiningPlayer
import org.bukkit.scheduler.BukkitRunnable

/**
 * Internal API class for updating blocks being broken by players each tick, as Minecraft does not natively
 * trigger an event each tick for this. Cannot be extended; do not tamper with.
 */
class BlockBreakTick(val blockworks : Blockworks) : BukkitRunnable() {
    val eventTarget = blockworks.plugin.server.pluginManager
    val subscribedInstances = mutableSetOf<BlockInstance>()

    override fun run() {
        subscribedInstances.forEach { instance ->
            eventTarget.callEvent(BlockInstanceTickEvent(instance, instance.breaker))
            if (instance.broken) {
                eventTarget.callEvent(BlockInstanceBreakEvent(instance, instance.breaker))
                unsubscribe(instance)
            }
        }
    }

    fun subscribe(blockInstance : BlockInstance) {
        subscribedInstances.add(blockInstance)
    }

    fun unsubscribe(blockInstance: BlockInstance) {
        subscribedInstances.remove(blockInstance)
    }
}