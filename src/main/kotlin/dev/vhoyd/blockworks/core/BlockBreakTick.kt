package dev.vhoyd.blockworks.core

import dev.vhoyd.blockworks.block.BlockDefinition
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.event.BlockInstanceBrokenEvent
import dev.vhoyd.blockworks.event.BlockInstanceTickEvent
import dev.vhoyd.blockworks.loot.DeterminedDrop
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

/**
 * Internal API class for updating blocks being broken by players each tick, as Minecraft does not natively
 * trigger an event each tick for this. Attempting to tamper with is ill-advised.
 */
internal class BlockBreakTick(val blockworks : Blockworks) : BukkitRunnable() {
    val manager = blockworks.plugin.server.pluginManager

    // set, so that duplicates aren't ticked twice (end-users could implement manually)
    val subscribedInstances = mutableSetOf<BlockInstance>()

    val log = blockworks.logger.context("BlockBreakTick")

    override fun run() {
        for (instance : BlockInstance in subscribedInstances) {
            manager.callEvent(BlockInstanceTickEvent(instance))
            if (instance.broken) handleBreakLogic(instance)
        }

    }

    // add new instance to set
    fun subscribe(blockInstance : BlockInstance) {
        log.debug("BlockInstance of type ${blockInstance.location.block.blockData.material} subscribed")
        subscribedInstances.add(blockInstance)
    }

    // remove instance from set (probably because it was broken)
    fun unsubscribe(blockInstance: BlockInstance) {
        val removed = subscribedInstances.remove(blockInstance)

        // TODO: figure out why this happens way more often than it should
        if (!removed) log.warn("Instance was not removed.")

        else log.debug("BlockInstance of type ${blockInstance.location.block.blockData.material} unsubscribed")
    }


    // break block like vanilla Minecraft
    fun applyVanillaBreak(location : Location) : BlockInstance? {

        if (subscribedInstances.isEmpty()) {
            log.warn("No subscribed blocks to break.")
            return null

        } else {
            var entry : BlockInstance? = null
            subscribedInstances.forEach {

                // either block was not meant to be vanilla or it's not at the spot the block was broken
                if (it.definition.breakCondition != BlockDefinition.VANILLA_BREAK_CONDITION || it.location != location) return@forEach

                // if the above checked didn't return, the block has been found
                log.debug("Found matching vanilla block break condition at ${location}.")
                entry = it
            }
            if (entry == null) {
                log.warn("No BlockInstance found at $location")
                return null
            } else return entry
        }
    }

    // do all the fun stuff related to breaking the block
    fun handleBreakLogic(instance : BlockInstance) {
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
        val event = BlockInstanceBrokenEvent(DeterminedDrop(instance, list, sumXp))
        manager.callEvent(event)
        instance.location.block.type = instance.replacementMaterial
        unsubscribe(instance)
        log.debug("Calling block break behavior.")
        instance.breakBlock()
        log.debug("Calling block drop behavior.")
        instance.dropBehavior(event.drops, instance)
        log.debug("End of break logic.")
    }
}