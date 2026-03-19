package dev.vhoyd.blockworks.api.block

import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.internal.InternalBlockInstance
import org.bukkit.Location

/**
 * Class for handling interaction between a [BlockDefinition]'s data and actual gameplay.
 * Think of it as `BlockDefinition`s being classes while `BlockInstance`s are objects of those classes.
 * @property definition the [BlockDefinition] that models this custom block instance.
 * @property location the [Location] of the instance. This is also the only thing used to check if two
 * `BlockInstance`s are equal to each other via [BlockInstance.equals]
 * @property breaker the [BlockBreaker] currently mining this `BlockInstance`.
 * @property broken whether this instance has been broken. Default implementation is
 * `definition.breakIf.test(this)`
 */
interface BlockInstance : Attributable {

    val definition: BlockDefinition
    val location: Location
    val breaker : BlockBreaker<*>
    val broken : Boolean
        get() = definition.breakIf.test(this)

    companion object {
        operator fun invoke(
            definition: BlockDefinition,
            location: Location,
            breaker: BlockBreaker<*>,
        ) : BlockInstance = InternalBlockInstance(definition, location, breaker)

        @JvmStatic
        @Suppress("unused") // for external use only
        fun create(
            definition: BlockDefinition,
            location: Location,
            breaker: BlockBreaker<*>,
        ) : BlockInstance = InternalBlockInstance(definition, location, breaker)
    }

}