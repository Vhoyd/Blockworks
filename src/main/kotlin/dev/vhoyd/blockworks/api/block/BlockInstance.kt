package dev.vhoyd.blockworks.api.block

import dev.vhoyd.blockworks.api.core.appendMap
import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.internal.InternalAttributed
import org.bukkit.Location

/**
 * Class for handling interaction between a [BlockDefinition]'s data and actual gameplay.
 * Think of it as `BlockDefinition`s being classes while `BlockInstance`s are objects of those classes.
 * @property definition the [BlockDefinition] that models this custom block instance.
 * @property location the [Location] of the instance. This is also the only thing used to check if two
 * `BlockInstance`s are equal to each other via [BlockInstance.equals]
 * @property breaker the [BlockBreaker] currently mining this `BlockInstance`.
 * @property broken whether this instance has been broken.
 * @property drops a shorthand for `definition.possibleDrops`
 * @property attributes the attribute data assigned to this instance. Copies `definition.attributes` initially.
 */
class BlockInstance internal constructor(
    val definition: BlockDefinition,
    val location: Location,
    val breaker : BlockBreaker<*>,
    private val attributed: Attributable = InternalAttributed(BlockInstance::class.java, definition.attributes)
) : Attributable by attributed {

    val breakCondition = definition.breakIf ?: breaker.blockworks.config.defaultBreakCondition
    val replacement = definition.replacement ?:

    if (definition.attributes.keys.contains(BlockDefinition.vanillaDmg)) location.block.type
    else breaker.blockworks.config.defaultReplacementMaterial

    val dropBehavior = definition.onDrop ?: breaker.blockworks.config.defaultDropBehavior
    val broken : Boolean
        get() = breakCondition.test(this)
    val drops = definition.drops

    /**
     * Shorthand for `definition.breakBehavior(this)`; does not set this instance's state to broken.
      */
    fun breakBlock() : Unit = definition.onBreak.accept(this)


    override infix fun equals(other: Any?): Boolean {
        if (other !is BlockInstance) return false
        return other.location == location
    }


    override fun toString(): String {
        return StringBuilder("BlockInstance(")
            .append(location.world.name)
            .append("@")
            .append(location.toVector())
            .append("::")
            .append(location.block.type)
            .append(" via ")
            .append(breaker.delegate!!::class.java.simpleName)
            .append("; attributes: ")
            .appendMap(attributes)
            .toString()
    }

    override fun hashCode(): Int {
        var result = definition.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + breaker.hashCode()
        result = 31 * result + attributed.hashCode()
        result = 31 * result + breakCondition.hashCode()
        result = 31 * result + replacement.hashCode()
        result = 31 * result + dropBehavior.hashCode()
        result = 31 * result + drops.hashCode()
        result = 31 * result + broken.hashCode()
        return result
    }

}