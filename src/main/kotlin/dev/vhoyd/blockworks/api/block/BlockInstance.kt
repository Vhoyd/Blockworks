package dev.vhoyd.blockworks.api.block

import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.api.model.Attributable
import org.bukkit.Location
import org.bukkit.Material

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
interface BlockInstance : Attributable {

    val definition: BlockDefinition;
    val location: Location;
    val breaker : BlockBreaker<*>;
    val broken : Boolean
    val replacement : Material

    /**
     * Shorthand for `definition.breakBehavior(this)`; does not set this instance's state to broken.
      */
    fun breakBlock()

}