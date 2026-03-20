package dev.vhoyd.blockworks.api.core

import dev.vhoyd.blockworks.api.event.BlockInstanceBreakAbortEvent
import dev.vhoyd.blockworks.api.event.BlockInstanceCreateEvent
import dev.vhoyd.blockworks.api.event.BlockInstanceStartBreakEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageAbortEvent
import org.bukkit.event.block.BlockDamageEvent

/**
 * Internal API class for handling most of the server -> plugin interactions. Should not be tinkered with under
 * most circumstances; not inheritable.
 */
internal class BukkitEventListener(private val blockworks: Blockworks) : Listener {
    private val dispatcher = blockworks.plugin.server.pluginManager
    private val blockDamage = blockworks.logger.context("Bukkit-BlockDamageEvent")
    private val damageAbort = blockworks.logger.context("Bukkit-BlockDamageAbortEvent")
    private val blockBreak = blockworks.logger.context("Bukkit-BlockBreakEvent")


    @EventHandler
    fun onBlockHit(e: BlockDamageEvent) {

        if (e.isCancelled) {
            blockDamage.debug("Ignoring canceled event.")
            return
        }

        val blockBreaker = blockworks.getBlockBreaker(e.player) ?: run {
            blockDamage.warn("No BlockBreaker object found for ${e.player.name}")
            return
        }

        val blockDefinition = blockworks.getDefinition(e.block, blockBreaker) ?: run {
            blockDamage.warn("Definition for block at ${e.block.location} not found.")
            return
        }


        val blockInstance = blockDefinition.createInstance(e.block, blockBreaker)

        val createEvent = BlockInstanceCreateEvent(blockInstance)
        dispatcher.callEvent(createEvent)
        if (createEvent.isCancelled) run {
            blockDamage.debug("Creation event canceled, aborting.")
            return
        }

        val startEvent = BlockInstanceStartBreakEvent(blockBreaker, blockInstance)
        dispatcher.callEvent(startEvent)
        if (startEvent.isCancelled) run {
            blockDamage.debug("Called event is canceled, aborting.")
            blockBreaker.currentBlock = null
            return
        }

        blockBreaker.currentBlock?.let {
            blockworks.blockInstanceRegistry.unsubscribe(it)
        }
        blockBreaker.currentBlock = startEvent.target
        blockworks.blockInstanceRegistry.subscribe(blockInstance)


    }

    @EventHandler
    fun onPlayerStopHittingBlock(e: BlockDamageAbortEvent) {

        val miningPlayer = blockworks.getBlockBreaker(e.player) ?: run {
            damageAbort.warn("No BlockBreaker object found for ${e.player.name}")
            return
        }
        val instance = miningPlayer.currentBlock ?: run {
            damageAbort.warn("BlockBreaker was not mining any BlockInstance.")
            return
        }

        val abortEvent = BlockInstanceBreakAbortEvent(instance)
        e.player.server.pluginManager.callEvent(abortEvent)
        if (abortEvent.isCancelled) run {
            damageAbort.warn("Called event is canceled, aborting.")
            return
        }

        blockworks.blockInstanceRegistry.unsubscribe(instance)
    }

    @EventHandler
    fun onBreak(e: BlockBreakEvent) {

        if (e.isCancelled) {
            blockBreak.debug("Ignoring canceled event.")
            return
        }



        val foundMatch = blockworks.blockInstanceRegistry.findVanilla(e.block.location)
        if (foundMatch != null) {
            val breaker = blockworks.getBlockBreaker(e.player) ?: run {
                blockBreak.warn("No BlockBreaker object found for ${e.player.name}")
                return
            }

            blockworks.blockInstanceRegistry.vanillaBreak(foundMatch, breaker)
        } else {
            blockBreak.debug("Event ignored due to no matching vanilla block.")
        }
    }

}