package dev.vhoyd.blockworks.mining

import dev.vhoyd.blockworks.core.Blockworks
import dev.vhoyd.blockworks.block.BlockInstance
import dev.vhoyd.blockworks.util.EmptyValue.Companion.BLOCKINSTANCE
import org.bukkit.entity.Player

// TODO: dependency injection for value math

/**
 * Class for tracking extra library data about any Player object, such as mining speed, current target block, etc.
 */
class MiningPlayer {
    var currentBlock: BlockInstance = BLOCKINSTANCE
    var heldItem: MiningTool?
    var breakingPower: Int
        get() = (heldItem?.breakingPower ?: 0)
        set(value) {internalbreakingpower = value - (heldItem?.breakingPower ?: 0)}
    var miningSpeed : Int
        get() = internalspeed + (heldItem?.miningSpeed ?: 0)
        set(value) {internalspeed = value -(heldItem?.miningFortune ?: 0);}
    var miningFortune : Int
        get() = internalfortune + (heldItem?.miningFortune ?: 0)
        set(value) {internalfortune = value - (heldItem?.miningFortune ?: 0);}
    val minecraftPlayer : Player

    private var internalspeed : Int = 0
    private var internalfortune : Int = 0
    private var internalbreakingpower : Int = 0

    constructor(player: Player, blockworks: Blockworks) : this(player, blockworks, 0, 0, 0)

    constructor(player: Player, blockworks: Blockworks, miningSpeed : Int, miningFortune: Int, breakingPower : Int) {
        this.minecraftPlayer = player
        this.internalspeed = miningSpeed
        this.internalfortune = miningFortune
        this.internalbreakingpower = breakingPower
        heldItem = null
        blockworks.registerPlayer(this)
    }
}