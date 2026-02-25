package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.mining.BlockBreaker
import dev.vhoyd.blockworks.mining.Attributable
import dev.vhoyd.blockworks.mining.Attribute
import dev.vhoyd.blockworks.mining.MiningPlayer
import org.bukkit.Location

/**
 * Class for handling interaction between a [BlockDefinition]'s data and actual gameplay.
 * Think of it as `BlockDefinition`s being classes while `BlockInstance`s are objects of those classes.
 * @property definition the [BlockDefinition] that models this custom block instance.
 * @property location the [Location] of the instance. This is also the only thing used to check if two
 * `BlockInstance`s are equal to each other via [BlockInstance.equals]
 * @property breaker the [MiningPlayer] currently mining this `BlockInstance`.
 * @property broken whether this instance has been broken.
 * @property drops a shorthand for `definition.possibleDrops`
 * @property attributes the attribute data assigned to this instance. Copies `definition.attributes` initially.
 */
class BlockInstance(val definition: BlockDefinition, val location: Location, val breaker : BlockBreaker<*>) : Attributable {
    val broken : Boolean
        get() = definition.breakCondition(this)
    val drops = definition.possibleDrops
    val attributes: MutableMap<Attribute<*,*>, Any> = definition.attributes.toMutableMap()

    /**
     * Shorthand for `definition.breakBehavior(this)`; does not set this instance's state to broken.
      */
    fun breakBlock() = definition.breakBehavior(this)

    override fun <P : Any, C : Any> setAttribute(
        attribute: Attribute<P, C>,
        value: C
    ) {
        attributes[attribute] = value
    }

    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C {

        @Suppress("UNCHECKED_CAST")
        return attributes[attribute] as C
    }

    override fun equals(other: Any?): Boolean {
        if (other !is BlockInstance) return false
        return other.location == location
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

}