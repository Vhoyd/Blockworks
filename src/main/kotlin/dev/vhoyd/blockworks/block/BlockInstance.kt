package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.core.Config
import org.bukkit.Location

/**
 * Class for handling interaction between a [BlockDefinition]'s data and actual gameplay.
 * Think of it as `BlockDefinition.kt`s being classes while `BlockInstance`s are objects of those classes.
 */
class BlockInstance {

    val block: BlockDefinition
    var damage : Double = 0.0
    val location: Location
    var canDrop = true
    val config : Config
    val strength : Double

    /**
     * @param block the in-world [BlockDefinition] this tile  is supposed to represent
     * @param location the location of the tile being broken
     */
    constructor(block: BlockDefinition, location: Location, config: Config) {
        this.block = block
        this.location = location
        this.config = config
        strength = block.blockStrength * config.miningRateScale
    }

    /**
     * @return whether the total damage sustained exceeds the required damage to break the tile
     */
    fun isBroken(): Boolean {
        return (damage >= strength)
    }

    /**
     * Yields a range 0-1 for the current breaking stage of the tile.
     * Break stage values are 0-9 if using packts; multiply by 9 if needed.
     * See [dev.vhoyd.blockworks.internal.task.BlockBreakTick.run] for how this is used
     * @return the progress stage of the tile, from 0 to 1.
     */
    fun getProgress(): Float {
        return (damage / strength).toFloat()
    }
}