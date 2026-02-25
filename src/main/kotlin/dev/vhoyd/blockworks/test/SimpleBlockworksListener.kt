package dev.vhoyd.blockworks.test

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.BlockPosition
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.event.BlockInstanceBreakAbortEvent
import dev.vhoyd.blockworks.event.BlockInstanceBrokenEvent
import dev.vhoyd.blockworks.event.BlockInstanceStartBreakEvent
import dev.vhoyd.blockworks.event.BlockInstanceTickEvent
import dev.vhoyd.blockworks.mining.Tool
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import kotlin.math.ceil
import kotlin.math.round

class SimpleBlockworksListener(val blockworks: Blockworks) : Listener {

    val manager = ProtocolLibrary.getProtocolManager()!!


    @EventHandler
    fun <T> onBlockStart(e : BlockInstanceStartBreakEvent) {
        e.isCancelled = true
        e.blockInstance.breaker.currentBlock = e.blockInstance
    }

    @EventHandler
    fun onBlockStop(e : BlockInstanceBreakAbortEvent) {
        e.blockInstance.breaker.currentBlock = null
    }

    @EventHandler
    fun onBlockTick(e : BlockInstanceTickEvent) {
        val block = e.blockInstance
        val player = block.breaker
        var totalDamage = player[SimpleMiningAttribute.MINING_SPEED]
        totalDamage += player.getElement<Tool>()[SimpleMiningAttribute.MINING_SPEED] ?: 0f
        totalDamage *= blockworks.config[SimpleConfigProperty.MINING_RATE_SCALE]
        block[SimpleMiningAttribute.BLOCK_DAMAGE] += totalDamage.toInt()
        val damage = block[SimpleMiningAttribute.BLOCK_DAMAGE].toFloat()
        val strength = block[SimpleMiningAttribute.BLOCK_STRENGTH]
        generateBlockBreakPacket(player.delegateAs<Player>(), block.location, damage/strength)

    }

    private fun generateBlockBreakPacket(player : Player, location : Location, progress : Float) {
        val packet = PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)
        packet.integers.write(0, -player.entityId)
        packet.blockPositionModifier.write(0, BlockPosition(location.blockX, location.blockY, location.blockZ))
        packet.integers.write(1, (progress * 9).toInt())
        manager.sendServerPacket(player, packet)
    }

    @EventHandler
    fun onBlockBreak(e : BlockInstanceBrokenEvent) {
        val instance = e.lootYield.blockInstance
        val breaker = instance.breaker
        if (blockworks.config[SimpleConfigProperty.IGNORE_MINING_FORTUNE]) {
            var fortune = breaker[SimpleMiningAttribute.MINING_FORTUNE]
            val toolFortune = breaker.getElement<Tool>()[SimpleMiningAttribute.MINING_FORTUNE]
            fortune += toolFortune
            val fortuneScalar = fortune / 100

            // everyone always has 1x normal drops, intrinsically, so add 1 to multiplier so it's not ever 0
            val allMult = 1 + (fortuneScalar % 1)

            e.lootYield *= allMult

            val leftover = fortuneScalar - allMult + 1

            e.lootYield.items.forEach {
                it.amount += calculateBonus(it.amount, leftover)
            }
        }



        val newBlock = blockworks.getBlock(instance.location.block.type)
            breaker.currentBlock = if (newBlock != null) BlockInstance(newBlock, instance.location, breaker) else null
    }


    private fun calculateBonus(baseAmount : Int, fortune : Float ) : Int {
        if (fortune == 0f) return 0
        val roundingBonus = blockworks.config[SimpleConfigProperty.ROUNDING_METHOD]
        if (!blockworks.config[SimpleConfigProperty.DYNAMIC_FORTUNE_SCALING]) {

            return when (roundingBonus) {
                SimpleConfigProperty.FortuneRemainderMethod.CEILING -> ceil(fortune).toInt()
                SimpleConfigProperty.FortuneRemainderMethod.FLOOR -> 0
                SimpleConfigProperty.FortuneRemainderMethod.ROUND -> round(fortune).toInt()
                SimpleConfigProperty.FortuneRemainderMethod.RANDOM -> if (Math.random() <= fortune) 1 else 0
            }

        }


        val sum = baseAmount * fortune

        return when (roundingBonus) {
            SimpleConfigProperty.FortuneRemainderMethod.CEILING -> ceil(sum).toInt()
            SimpleConfigProperty.FortuneRemainderMethod.FLOOR -> sum.toInt()
            SimpleConfigProperty.FortuneRemainderMethod.ROUND -> round(sum).toInt()
            SimpleConfigProperty.FortuneRemainderMethod.RANDOM -> sum.toInt() + if (Math.random() <= sum % 1) 1 else 0
        }
    }





    @EventHandler
    fun onPlayerJoin(e : PlayerJoinEvent) {
        e.player.inventory.addItem(SimpleMain.testStick.delegate)
    }

}