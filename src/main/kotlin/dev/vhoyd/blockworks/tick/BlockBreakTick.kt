package dev.vhoyd.blockworks.tick

import dev.vhoyd.blockworks.block.BlockDefinition
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.event.BlockInstanceBrokenEvent
import dev.vhoyd.blockworks.event.BlockInstanceTickEvent
import dev.vhoyd.blockworks.loot.DeterminedDrop
import dev.vhoyd.blockworks.mining.BlockBreaker
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

/**
 * Internal API class for updating blocks being broken by players each tick, as Minecraft does not natively
 * trigger an event each tick for this. Cannot be extended; do not tamper with.
 */
class BlockBreakTick(val blockworks : Blockworks) : BukkitRunnable() {
    val eventTarget = blockworks.plugin.server.pluginManager
    val subscribedInstances = mutableSetOf<BlockInstance<*>>()
    val log = blockworks.logger.context("BlockBreakTick")

    override fun run() {
        for (instance : BlockInstance<*> in subscribedInstances) {
            eventTarget.callEvent(BlockInstanceTickEvent(instance))
            if (instance.broken) {
                handleBreakLogic(instance)
            }
        }

    }

    fun subscribe(blockInstance : BlockInstance<*>) {
        log.debug("BlockInstance of type ${blockInstance.definition.material} subscribed")
        subscribedInstances.add(blockInstance)
    }

    fun unsubscribe(blockInstance: BlockInstance<*>) {
        val removed = subscribedInstances.remove(blockInstance)
        if (!removed) {
            log.warn("Instance was not removed.")
        } else {
            log.debug("BlockInstance of type ${blockInstance.definition.material} unsubscribed")
        }
    }

    fun applyVanillaBreak(location : Location) : BlockInstance<*>? {
        if (subscribedInstances.isEmpty()) {
            log.warn("No subscribed blocks to break.")
            return null
        } else {
            var entry : BlockInstance<*>? = null
            for (it : BlockInstance<*> in subscribedInstances) {
                if (it.definition.breakCondition != BlockDefinition.VANILLA_BREAK_CONDITION || it.location != location) return null
                log.debug("Found matching vanilla block break condition at ${location}.")
                entry = it
            }
            if (entry == null) {
                log.warn("No BlockInstance found at $location")
                return null
            } else {
                return entry
            }
        }
    }

    fun handleBreakLogic(instance : BlockInstance<*>) {
        val list = mutableListOf<ItemStack>()
        val sumXp = mutableListOf<Int>()
        instance.drops.forEach {
            log.debug("Checking ConditionalDrop: $it")
            if (it.condition(instance)) {
                log.debug("Condition passed")
                val rand = it.dropPool.pickRandom()
                if (rand.type != Material.AIR) {
                    list.add(rand)
                } else {
                    log.debug("Drop is of type AIR, ignoring it.")
                }
                sumXp += it.expPool.pickRandom()
            } else { log.debug("Condition failed") }
        }
        val event = BlockInstanceBrokenEvent(DeterminedDrop(instance,list, sumXp))
        eventTarget.callEvent(event)
        instance.location.block.type = instance.definition.brokenMaterial
        unsubscribe(instance)
        log.debug("Calling block break behavior.")
        instance.breakBlock()
        log.debug("Calling block drop behavior.")
        instance.definition.dropBehavior(event.lootYield, instance)
        log.debug("End of break logic.")
    }
}