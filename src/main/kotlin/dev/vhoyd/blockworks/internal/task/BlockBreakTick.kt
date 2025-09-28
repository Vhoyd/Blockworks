package dev.vhoyd.blockworks.internal.task

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.wrappers.BlockPosition
import dev.vhoyd.blockworks.core.Config
import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.mining.MiningPlayer
import dev.vhoyd.blockworks.util.EmptyValue
import org.bukkit.Location
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable

class BlockBreakTick : BukkitRunnable {
    private val blockworks : Blockworks
    private val config : Config
    private val manager = ProtocolLibrary.getProtocolManager()

    constructor(blockworks : Blockworks) {
        this.blockworks = blockworks
        config = blockworks.config
    }

    private fun generateBlockBreakPacket(player : Player, location : Location, progress : Float) {
        val packet = PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION)
        packet.integers.write(0, -player.entityId)
        packet.blockPositionModifier.write(0, BlockPosition(location.blockX,location.blockY, location.blockZ))
        packet.integers.write(1, (progress * 9).toInt())
        manager.sendServerPacket(player, packet)
    }



    private fun calculateDropAmount(baseAmount : Int, fortune: Int) : Int {
        if (blockworks.config.ignoreMiningFortune) return baseAmount

        // everyone always has 1x normal drops, intrinsically, so add 1 to multiplier so it's not ever 0
        val fortuneMultiplier = 1 + fortune / 100

        var total = baseAmount * fortuneMultiplier
        val bonusFortune = fortune % 100
        val remainder : Double

        val fortuneAsPercentage = bonusFortune.toDouble() / 100

        if (blockworks.config.dynamicFortuneScaling) {
            val bonus = fortuneAsPercentage * baseAmount
            total += bonus.toInt()
            remainder = (bonus % 1) * 100
        } else {
            remainder = fortuneAsPercentage
        }
        if (Math.random() <= remainder) total += baseAmount

        return total
    }

    override fun run() {
        for (player : MiningPlayer in blockworks.players) {
            val currentBlock = player.currentBlock
            if (currentBlock != EmptyValue.BLOCKINSTANCE) {
                //increase damage progress
                currentBlock.damage += player.miningSpeed
                player.minecraftPlayer.server.consoleSender.sendMessage(currentBlock.damage.toString())
                player.minecraftPlayer.server.consoleSender.sendMessage(currentBlock.strength.toString())
                /*
                 * getProgress returns 0-9, break stage values are 0-9
                 * since blocks start off with no animation, an extraneous value (I used -1) is needed
                 */
                generateBlockBreakPacket(player.minecraftPlayer, currentBlock.location, currentBlock.getProgress())

                if (currentBlock.isBroken() && currentBlock.canDrop) {

                    //remove block
                    currentBlock.canDrop = false

                    currentBlock.location.block.type = currentBlock.block.brokenMaterial
                    generateBlockBreakPacket(player.minecraftPlayer, currentBlock.location, 0f)

                    //play registered action at broken block
                    currentBlock.block.brokenAction.run(currentBlock, player)

                    //create loot for drops
                    for (conditionalDrop in currentBlock.block.possibleDrops) {

                        if (conditionalDrop.condition(player)) {

                            // drop exp if any
                            val expValue = conditionalDrop.exp.pickRandom()
                            if (expValue > 0) {
                                val orb = player.minecraftPlayer.world.spawn(
                                    currentBlock.location.add(conditionalDrop.locationOffset),
                                    ExperienceOrb::class.java
                                )
                                orb.experience = expValue
                            }

                            // drop loot if any
                            val droppedItem = conditionalDrop.drops.pickRandom().clone()
                            val baseAmount = droppedItem.amount
                            if (baseAmount > 0) {
                                var quantity = calculateDropAmount(baseAmount, player.miningFortune)
                                while (quantity > 64) {
                                    quantity -= 64
                                    droppedItem.amount = 64
                                    player.minecraftPlayer.world.dropItemNaturally(
                                        currentBlock.location,
                                        droppedItem.clone()
                                    )
                                }

                                if (quantity > 0) {
                                    droppedItem.amount = quantity
                                    player.minecraftPlayer.world.dropItemNaturally(currentBlock.location.add(conditionalDrop.locationOffset), droppedItem)
                                }
                            }
                        }
                    }
                    val newBlock = blockworks.getBlock(currentBlock.location.block.type)
                    if (newBlock != EmptyValue.BLOCKDEFINITION) {
                        player.currentBlock = BlockInstance(newBlock, currentBlock.location, blockworks.config)
                    }
                }

            }
        }
    }
}