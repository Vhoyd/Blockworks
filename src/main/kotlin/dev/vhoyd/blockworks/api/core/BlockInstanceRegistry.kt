package dev.vhoyd.blockworks.api.core

import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.event.BlockInstanceBrokenEvent
import dev.vhoyd.blockworks.api.event.BlockInstanceCreateEvent
import dev.vhoyd.blockworks.api.event.BlockInstanceTickEvent
import dev.vhoyd.blockworks.api.event.VanillaInstanceBrokenEvent
import dev.vhoyd.blockworks.api.loot.DeterminedDrop
import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.internal.VanillaDefinition
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

/**
 * Internal API class for updating blocks being broken each tick, as Minecraft does not natively
 * trigger an event each tick for this. Use with caution.
 */
class BlockInstanceRegistry internal constructor(val blockworks : Blockworks) : BukkitRunnable() {
    private val dispatcher = blockworks.plugin.server.pluginManager

    // set instead of list, so that duplicates aren't ticked twice (end-users could implement manually)
    private val subscribers = mutableSetOf<BlockInstance>()
    private val toDelete: MutableSet<BlockInstance> = mutableSetOf()
    private val toAdd: MutableSet<BlockInstance> = mutableSetOf()

    private val log = blockworks.logger.context("BlockInstanceRegistry")

    override fun run() {
        subscribers.forEach {
            dispatcher.callEvent(BlockInstanceTickEvent(it))
            if (it.broken) breakBlock(it)
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


    internal fun findVanilla(location : Location) : BlockInstance? {

        if (subscribers.isEmpty()) {
            log.warn("No subscribed blocks to break.")
            return null

        }

        subscribers.forEach {

            if (it.location == location && VanillaDefinition::class.java.isInstance(it.definition)) {
                log.debug("Found qualifying vanilla block for ${location}.")
                return it
            }

        }

        log.warn("No vanilla BlockInstance found at $location")
        return null

    }

    // do all the fun stuff related to breaking the block
    internal fun breakBlock(instance : BlockInstance) {
        val drop = createDrop(instance)
        val brokenEvent = BlockInstanceBrokenEvent(drop, instance, instance.definition.replacement)

        dispatcher.callEvent(brokenEvent)
        if (brokenEvent.isCancelled) {
            log.info("Break event canceled, aborting.")
            return
        }

        unsubscribe(instance)
        log.debug("Calling block break behavior.")
        instance.definition.onBreak.accept(instance)

        val block = instance.location.block
        block.type = brokenEvent.replacement

        blockworks.getDefinition(block, instance.breaker)?.let { definition ->
            val new = definition.createInstance(block, instance.breaker)
            val createEvent = BlockInstanceCreateEvent(new)
            dispatcher.callEvent(createEvent)
            if (brokenEvent.isCancelled) {
                log.info("CreateEvent canceled, aborting.")
                return@let
            }
            instance.breaker.currentBlock = new
            subscribe(new)
        }


        log.debug("Calling block drop behavior.")
        instance.definition.onDrop.accept(brokenEvent.drops)
        log.debug("End of break logic.")
    }

    internal fun vanillaBreak(instance : BlockInstance, breaker: BlockBreaker<Player>) {
        val brokenEvent = VanillaInstanceBrokenEvent( instance, breaker)
        dispatcher.callEvent(brokenEvent)
        if (brokenEvent.isCancelled) {
            log.info("Vanilla break event canceled, aborting.")
            return
        }

        unsubscribe(instance)

        log.debug("Calling block break behavior.")
        instance.definition.onBreak.accept(instance)

        val block = instance.location.block
        block.type = Material.AIR


        log.debug("End of vanilla break logic.")

    }

    private fun createDrop(block : BlockInstance) : DeterminedDrop {
        val set = mutableSetOf<ItemStack>()
        val sumXp = mutableSetOf<Int>()
        block.definition.drops.forEach {
            log.debug("Checking ConditionalDrop: $it")
            if (it.condition.test(block)) {
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
        return DeterminedDrop(block, set, sumXp)
    }
}