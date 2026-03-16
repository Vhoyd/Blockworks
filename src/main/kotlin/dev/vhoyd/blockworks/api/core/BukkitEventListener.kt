package dev.vhoyd.blockworks.api.core

import dev.vhoyd.blockworks.api.block.BlockDefinition
import dev.vhoyd.blockworks.api.event.BlockInstanceBreakAbortEvent
import dev.vhoyd.blockworks.api.event.BlockInstanceStartBreakEvent
import dev.vhoyd.blockworks.api.event.BlockInstanceTickEvent
import dev.vhoyd.blockworks.api.model.delegateAs
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDamageAbortEvent
import org.bukkit.event.block.BlockDamageEvent
import org.bukkit.potion.PotionEffectType
import kotlin.experimental.and
import kotlin.math.pow

/**
 * Internal API class for handling most of the server -> plugin interactions. Should not be tinkered with under
 * most circumstances; not inheritable.
 */
internal class BukkitEventListener(private val blockworks : Blockworks) : Listener {

    private val blockDamage = blockworks.logger.context("Bukkit-BlockDamageEvent")
    private val damageAbort = blockworks.logger.context("Bukkit-BlockDamageAbortEvent")
    private val blockBreak = blockworks.logger.context("Bukkit-BlockBreakEvent")
    private val instanceTick = blockworks.logger.context("Bukkit-BlockInstanceTickEvent")
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
            blockworks.breakers.forEach { blockDamage.debug(it.delegate) }
            return
        }

        if (blockBreaker.currentBlock != null) {
            blockDamage.debug("unsubscribing previous BlockInstance at $blockBreaker")
            blockworks.blockInstanceManager.unsubscribe(blockBreaker.currentBlock!!)
        } else {
            blockDamage.debug("BlockworksPlayer was not previously mining any BlockInstance.")
        }

        val blockDefinition = blockworks.getDefinition(e.block, blockBreaker) ?: run {

            blockDamage.warn("Definition for type ${e.block.type} not found.")
            return
        }

        val blockInstance = blockDefinition.createInstance(e.block, blockBreaker)
        e.player.server.pluginManager.callEvent(BlockInstanceStartBreakEvent(blockInstance))
        blockBreaker.currentBlock = blockInstance
        blockworks.blockInstanceManager.subscribe(blockInstance)
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
        blockworks.blockInstanceManager.unsubscribe(instance)
    }

    @EventHandler
    fun onBreak( e : BlockBreakEvent) {

        if (eventMask and Config.EventMaskType.BLOCK_BREAK.mask == zero) {
            blockBreak.debug("Event ignored.")
            e.isCancelled = false
            return
        }

        val foundMatch = blockworks.blockInstanceManager.applyVanillaBreak(e.block.location)
        if (foundMatch != null ) {
            if (eventMask and Config.EventMaskType.BLOCK_BREAK_MATCH.mask == zero) {
                blockBreak.debug("Event ignored.")
                e.isCancelled = false
                return
            }
            e.isCancelled = true
            blockworks.blockInstanceManager.handleBreakLogic(foundMatch)
        } else {
            blockBreak.debug("Event ignored due to no matching vanilla block.")
        }
    }

    @EventHandler
    fun onInstanceTick(e : BlockInstanceTickEvent) {
        val target = e.target
        val player = e.target.breaker.delegateAs<Player>() ?: return
        target[BlockDefinition.vanillaDmg]?.let { damage ->
            instanceTick.debug("Vanilla instance of type ${target.location.block.type} detected at ${target.location}")
            val hasteEffect = player.getPotionEffect(PotionEffectType.HASTE)?.amplifier?.let { it + 1} ?: 0
            instanceTick.debug("Player haste level: $hasteEffect")
            val fatigueEffect = player.getPotionEffect(PotionEffectType.MINING_FATIGUE)?.amplifier?.let { it + 1} ?: 0
            instanceTick.debug("Player fatigue level: $fatigueEffect")
            val hasteMult = if (!target[BlockDefinition.vanillaHaste]!!) 0.2f * hasteEffect + 1  else 1
            instanceTick.debug("Haste multiplier: $hasteMult")
            val fatigueMult = if (!target[BlockDefinition.vanillaFatigue]!!) 0.3.pow(
                fatigueEffect.coerceAtMost(4).toDouble()
            ) else 1
            instanceTick.debug("Fatigue multiplier: $fatigueMult")
            val dmg = (e.target.location.block.getBreakSpeed(player) / hasteMult.toFloat()) / fatigueMult.toFloat()
            val total = damage + dmg * 10f
            instanceTick.debug("Final damage done: $dmg")
            instanceTick.debug("Total damage so far: $total")
            player.sendBlockDamage(target.location, total.coerceAtMost(1f), -player.entityId)
            target[BlockDefinition.vanillaDmg] = total

        }
    }

}