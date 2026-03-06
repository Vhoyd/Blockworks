package dev.vhoyd.blockworks.core

import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.event.BlockInstanceBreakAbortEvent
import dev.vhoyd.blockworks.event.BlockInstanceStartBreakEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageAbortEvent
import org.bukkit.event.block.BlockDamageEvent
import kotlin.experimental.and

/**
 * Internal API class for handling most of the server -> plugin interactions. Should not be tinkered with under
 * most circumstances; not inheritable.
 */
internal class BukkitEventListener(private val blockworks : Blockworks) : Listener {

    private val blockDamage = blockworks.logger.context("Bukkit-BlockDamageEvent")
    private val damageAbort = blockworks.logger.context("Bukkit-BlockDamageAbortEvent")
    private val blockBreak = blockworks.logger.context("Bukkit-BlockBreakEvent")
    private val eventMask = blockworks.config.eventMask
    private val zero : Byte = 0


    @EventHandler
    fun onBlockHit(e : BlockDamageEvent) {

        if (eventMask and Config.EventMaskType.BLOCK_DAMAGE.mask == zero) {
            e.isCancelled = false
            blockDamage.debug("Event ignored.")
            return
        }

        blockDamage.debug("${e.player.name} started mining at ${e.block.location}")

        val blockBreaker = blockworks.getBlockBreaker(e.player) ?: run {
            blockDamage.warn("No BlockworksPlayer object found for ${e.player.name}")
            return
        }

        if (blockBreaker.currentBlock != null) {
            blockDamage.debug("unsubscribing previous BlockInstance at $blockBreaker")
            blockworks.breakTick.unsubscribe(blockBreaker.currentBlock!!)
        } else {
            blockDamage.debug("BlockworksPlayer was not previously mining any BlockInstance.")
        }

        val blockDefinition = blockworks.getDefinition(e.block, blockBreaker) ?: run {

            blockDamage.warn("Definition for type ${e.block.type} not found.")
            return
        }

        val blockInstance = BlockInstance(blockDefinition, e.block.location, blockBreaker)
        e.player.server.pluginManager.callEvent(BlockInstanceStartBreakEvent(blockInstance))
        blockworks.breakTick.subscribe(blockInstance)
    }

    @EventHandler
    fun onPlayerStopHittingBlock(e : BlockDamageAbortEvent) {

        val miningPlayer = blockworks.getBlockBreaker(e.player) ?: run {
            damageAbort.warn("No BlockBreaker object found for ${e.player.name}")
            return
        }
        val instance = miningPlayer.currentBlock ?: run {
            damageAbort.warn("BlockBreaker was not mining any BlockInstance.")
            return
        }
        e.player.server.pluginManager.callEvent(BlockInstanceBreakAbortEvent(instance))
        blockworks.breakTick.unsubscribe(instance)
    }

    @EventHandler
    fun onBreak( e : BlockBreakEvent) {

        if (eventMask and Config.EventMaskType.BLOCK_BREAK.mask == zero) {
            blockBreak.debug("Event ignored.")
            e.isCancelled = false
            return
        }

        val foundMatch = blockworks.breakTick.applyVanillaBreak(e.block.location)
        if (foundMatch != null ) {
            if (eventMask and Config.EventMaskType.BLOCK_BREAK_MATCH.mask == zero) {
                blockBreak.debug("Event ignored.")
                e.isCancelled = false
                return
            }
            e.isCancelled = true
            blockworks.breakTick.handleBreakLogic(foundMatch)
        } else {
            blockBreak.debug("Event ignored due to no matching vanilla block.")
        }
    }

}