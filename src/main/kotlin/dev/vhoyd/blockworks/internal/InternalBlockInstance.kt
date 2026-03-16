package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.block.BlockDefinition
import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.core.appendMap
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.BlockBreaker
import org.bukkit.Location

internal class InternalBlockInstance(
    override val definition: BlockDefinition,
    override val location: Location,
    override val breaker : BlockBreaker<*>,
    private val attributed: Attributable = InternalAttributed(InternalBlockInstance::class.java, definition.attributes)
) : Attributable by attributed, BlockInstance {

    val breakCondition = definition.breakIf ?: breaker.blockworks.config.defaultBreakCondition
    override val replacement = definition.replacement ?:

    if (definition.attributes.keys.contains(BlockDefinition.vanillaDmg)) location.block.type
    else breaker.blockworks.config.defaultReplacementMaterial

    val dropBehavior = definition.onDrop ?: breaker.blockworks.config.defaultDropBehavior
    override val broken : Boolean
        get() = breakCondition.test(this)
    val drops = definition.drops

    /**
     * Shorthand for `definition.breakBehavior(this)`; does not set this instance's state to broken.
     */
    override fun breakBlock() : Unit = definition.onBreak.accept(this)


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