package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.block.BlockDefinition
import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.core.appendMap
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.BlockBreaker
import org.bukkit.Location


/**
 * Default implementation of [BlockInstance].
 *
 * [equals] only compares Locations.
 */
internal class InternalBlockInstance(
    override val definition: BlockDefinition,
    override val location: Location,
    override val breaker: BlockBreaker<*>,
    private val attributed: Attributable = InternalAttributed(definition.attributes)
) : Attributable by attributed, BlockInstance {


    override val broken: Boolean
        get() = definition.breakIf.test(this)


    override infix fun equals(other: Any?): Boolean {
        return other is BlockInstance && other.location == location
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

    // Thanks to IntelliJ for crying about me not adding this until I let it auto-generate
    override fun hashCode(): Int {
        var result = definition.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + breaker.hashCode()
        result = 31 * result + attributed.hashCode()
        result = 31 * result + broken.hashCode()
        return result
    }

}