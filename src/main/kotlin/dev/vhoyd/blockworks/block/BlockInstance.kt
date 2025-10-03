package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.mining.BlockworksAttributable
import dev.vhoyd.blockworks.mining.MiningAttribute
import dev.vhoyd.blockworks.mining.MiningPlayer
import org.bukkit.Location

/**
 * Class for handling interaction between a [BlockDefinition]'s data and actual gameplay.
 * Think of it as `BlockDefinition.kt`s being classes while `BlockInstance`s are objects of those classes.
 */
class BlockInstance(val definition: BlockDefinition, val location: Location, val breaker : MiningPlayer) : BlockworksAttributable{
    val broken : Boolean
        get() = definition.breakCondition(this)
    val attributes: MutableMap<MiningAttribute<*,*>, Any> = definition.attributes.toMutableMap()

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