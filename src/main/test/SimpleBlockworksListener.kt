package dev.vhoyd.blockworks.simple

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.BlockPosition
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.event.BlockInstanceBreakAbortEvent
import dev.vhoyd.blockworks.event.BlockInstanceBreakEvent
import dev.vhoyd.blockworks.event.BlockInstanceStartBreakEvent
import dev.vhoyd.blockworks.event.BlockInstanceTickEvent
import dev.vhoyd.blockworks.loot.ConditionalDrop
import dev.vhoyd.blockworks.util.EmptyValue
import org.bukkit.Location
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.math.ceil
import kotlin.math.round

class SimpleBlockworksListener : Listener {

    val blockworks : Blockworks
    val manager = ProtocolLibrary.getProtocolManager()

    constructor(blockworks: Blockworks) {
        this.blockworks = blockworks
    }

    @EventHandler
    fun onBlockStart(e : BlockInstanceStartBreakEvent) {
        e.isCancelled = true
        if (e.blockInstance != EmptyValue.BLOCKINSTANCE) {
            e.miningPlayer.currentBlock = e.blockInstance
        }
    }

    @EventHandler
    fun onBlockStop(e : BlockInstanceBreakAbortEvent) {
        e.miningPlayer.currentBlock = EmptyValue.BLOCKINSTANCE
    }

    @EventHandler
    fun onBlockTick(e : BlockInstanceTickEvent) {
        var totalDamage = e.miningPlayer[SimpleMiningAttribute.MINING_SPEED]
        totalDamage += e.miningPlayer.heldItem?.get(SimpleMiningAttribute.MINING_SPEED) ?: 0f
        totalDamage *= blockworks.config[SimpleConfigProperty.MINING_RATE_SCALE]
        e.blockInstance[SimpleMiningAttribute.BLOCK_DAMAGE] += totalDamage.toInt()
        val damage = e.blockInstance[SimpleMiningAttribute.BLOCK_DAMAGE].toFloat()
        val strength = e.blockInstance[SimpleMiningAttribute.BLOCK_STRENGTH]
        generateBlockBreakPacket(e.miningPlayer.minecraftPlayer, e.blockInstance.location, damage/strength)

    }

    private fun generateBlockBreakPacket(player : Player, location : Location, progress : Float) {
        val packet = PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)
        packet.integers.write(0, -player.entityId)
        packet.blockPositionModifier.write(0, BlockPosition(location.blockX, location.blockY, location.blockZ))
        packet.integers.write(1, (progress * 9).toInt())
        manager.sendServerPacket(player, packet)
    }

    @EventHandler
    fun onBlockBreak(e : BlockInstanceBreakEvent) {
        var fortune = e.miningPlayer[SimpleMiningAttribute.MINING_FORTUNE]
        val toolFortune = e.miningPlayer.heldItem?.get(SimpleMiningAttribute.MINING_FORTUNE) ?: 0f
        fortune += toolFortune

        for (drop in e.blockInstance.definition.possibleDrops) {
            if (!drop.condition(e.miningPlayer)) continue
            dropExp(drop, e.blockInstance)
            dropReward(drop, fortune, e.blockInstance)

        }

        val newBlock = blockworks.getBlock(e.blockInstance.location.block.type)
        if (newBlock != EmptyValue.BLOCKDEFINITION) {
            e.miningPlayer.currentBlock = BlockInstance(newBlock, e.blockInstance.location, e.miningPlayer)
        }
    }


    private fun dropReward(drop : ConditionalDrop, fortune : Float, currentBlock : BlockInstance) {
        val reward = drop.drops.pickRandom().clone()
        if (reward.amount > 0) {
            var quantity = calculateDropAmount(reward.amount, fortune)
            while (quantity > 64) {
                quantity -= 64
                reward.amount = 64
                currentBlock.location.world.dropItemNaturally(
                    currentBlock.location,
                    reward.clone()
                )
            }

            if (quantity > 0) {
                reward.amount = quantity
                currentBlock.location.world.dropItemNaturally(currentBlock.location.add(drop.locationOffset), reward)
            }
        }

    }

    private fun dropExp(drop : ConditionalDrop, currentBlock : BlockInstance) {
        // drop exp if any
        val expValue = drop.exp.pickRandom()
        if (expValue > 0) {
            val orb = currentBlock.location.world.spawn(
                currentBlock.location.add(drop.locationOffset),
                ExperienceOrb::class.java
            )
            orb.experience = expValue
        }
    }


    private fun calculateDropAmount(baseAmount : Int, fortune: Float) : Int {
        if (blockworks.config[SimpleConfigProperty.IGNORE_MINING_FORTUNE]) return baseAmount

        // everyone always has 1x normal drops, intrinsically, so add 1 to multiplier so it's not ever 0
        val fortuneMultiplier = 1 + fortune / 100

        val roundingBonus = blockworks.config[SimpleConfigProperty.ROUNDING_METHOD]

        val sum = baseAmount * fortuneMultiplier

        var total = when (roundingBonus) {
            SimpleConfigProperty.RoundingMethod.CEILING -> ceil(sum).toInt()
            SimpleConfigProperty.RoundingMethod.FLOOR -> sum.toInt()
            SimpleConfigProperty.RoundingMethod.ROUND -> round(sum).toInt()
        }

        val bonusFortune = fortune % 100
        val remainder : Double

        val fortuneAsPercentage = bonusFortune.toDouble() / 100

        if (blockworks.config[SimpleConfigProperty.DYNAMIC_FORTUNE_SCALING]) {
            val bonus = fortuneAsPercentage * baseAmount
            total += bonus.toInt()
            remainder = (bonus % 1) * 100
        } else {
            remainder = fortuneAsPercentage
        }
        if (Math.random() <= remainder) total += baseAmount

        return total
    }





    @EventHandler
    fun onPlayerJoin(e : PlayerJoinEvent) {
        e.player.inventory.addItem(SimpleMain.testStick.itemStack)
    }

}