package dev.vhoyd.blockworks.api.core

import dev.vhoyd.blockworks.api.block.BlockDefinition
import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.event.BlockInstanceBrokenEvent
import dev.vhoyd.blockworks.api.event.BlockInstanceTickEvent
import dev.vhoyd.blockworks.api.loot.DeterminedDrop
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

/**
 * Internal API class for updating blocks being broken each tick, as Minecraft does not natively
 * trigger an event each tick for this. Use with caution.
 */
class BlockInstanceRegistry internal constructor(val blockworks : Blockworks) : BukkitRunnable() {
    private val manager = blockworks.plugin.server.pluginManager

    // set instead of list, so that duplicates aren't ticked twice (end-users could implement manually)
    private val subscribers = mutableSetOf<BlockInstance>()
    private val toDelete: MutableSet<BlockInstance> = mutableSetOf()
    private val toAdd: MutableSet<BlockInstance> = mutableSetOf()

    private val log = blockworks.logger.context("BlockInstanceRegistry")

    override fun run() {
        subscribers.forEach {
            manager.callEvent(BlockInstanceTickEvent(it))
            if (it.broken) handleBreakLogic(it)
        }
        toAdd.forEach {
            val added = subscribers.add(it)
            if (added) log.debug("BlockInstance at ${it.location} subscribed")
            else log.warn("BlockInstance at ${it.location} was not subscribed")
        }
        toAdd.clear()
        toDelete.forEach {
            val removed = subscribers.remove(it)
            it.breaker.currentBlock = null
            // TODO: figure out why this happens way more often than it should
            if (!removed) log.warn("BlockInstance at ${it.location} was not removed.")
            else log.debug("BlockInstance at ${it.location} unsubscribed")
        }
        toDelete.clear()

    }

    /**
     * Adds a [BlockInstance] to the set of ones that Blockworks will tick over and dispatch events for.
     * @return whether the `BlockInstance` was successfully added or not, as defined by [MutableSet.add]
     */
    fun subscribe(blockInstance : BlockInstance) {
        log.debug("BlockInstance at ${blockInstance.location} queued for subscription ")
        toAdd.add(blockInstance)
    }

    /**
     * Removes a [BlockInstance] from the set of ones being handled.
     * @return whether the `BlockInstance` was successfully removed or not, as defined by [MutableSet.remove]
     */
    fun unsubscribe(blockInstance: BlockInstance) {
        log.debug("BlockInstance at ${blockInstance.location} queued for removal ")
        toDelete.add(blockInstance)
    }


    // break block like vanilla Minecraft
    internal fun applyVanillaBreak(location : Location) : BlockInstance? {

        if (subscribers.isEmpty()) {
            log.warn("No subscribed blocks to break.")
            return null

        } else {
            var entry : BlockInstance? = null
            subscribers.forEach {

                // either block was not meant to be vanilla or it's not at the spot the block was broken
                if (it.definition.breakIf != BlockDefinition.vanillaBreakPredicate || it.location != location) return@forEach

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
    internal fun handleBreakLogic(instance : BlockInstance) {
        val set = mutableSetOf<ItemStack>()
        val sumXp = mutableSetOf<Int>()
        instance.definition.drops.forEach {
            log.debug("Checking ConditionalDrop: $it")
            if (it.condition.test(instance)) {
                log.debug("Condition passed")
                val rand = it.drops.pickRandom()
                if (rand.type != Material.AIR) {
                    set.add(rand.clone())
                } else {
                    log.debug("Drop is of type AIR, ignoring it.")
                }
                sumXp += it.exp.pickRandom()
            } else { log.debug("Condition failed") }
        }
        val event = BlockInstanceBrokenEvent(DeterminedDrop(instance, set, sumXp))
        manager.callEvent(event)
        val block = instance.location.block
        block.type = instance.definition.replacement
        blockworks.getDefinition(block, instance.breaker)?.let { definition ->
            val new = definition.createInstance(block, instance.breaker)
            instance.breaker.currentBlock = new
            subscribe(new)
        }
        unsubscribe(instance)
        log.debug("Calling block break behavior.")
        instance.definition.onBreak.accept(instance)
        log.debug("Calling block drop behavior.")
        instance.definition.onDrop.accept(event.drops)
        log.debug("End of break logic.")
    }
}