package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.mining.BlockworksAttributable
import dev.vhoyd.blockworks.mining.MiningAttribute
import dev.vhoyd.blockworks.mining.MiningPlayer
import org.bukkit.Location

/**
 * Class for handling interaction between a [BlockDefinition]'s data and actual gameplay.
 * Think of it as `BlockDefinition.kt`s being classes while `BlockInstance`s are objects of those classes.
 */
class BlockInstance : BlockworksAttributable{

    val definition: BlockDefinition
    val location: Location
    val breaker : MiningPlayer
    val attributes : MutableMap<MiningAttribute<*,*>, Any>

    /**
     * @param block the in-world [BlockDefinition] this tile  is supposed to represent
     * @param location the location of the tile being broken
     */
    constructor(block: BlockDefinition, location: Location, breaker : MiningPlayer) {
        this.definition = block
        this.location = location
        this.breaker = breaker
        this.attributes = definition.attributeMap.toMutableMap()
    }

    fun isBroken() : Boolean {
        return definition.breakCondition(this)
    }

    override fun <P : Any, C : Any> setAttribute(
        miningAttribute: MiningAttribute<P, C>,
        value: C
    ) {
        attributes[miningAttribute] = value
    }

    override fun <P : Any, C : Any> getAttribute(miningAttribute: MiningAttribute<P, C>): C {

        @Suppress("UNCHECKED_CAST")
        return attributes[miningAttribute] as C
    }

}