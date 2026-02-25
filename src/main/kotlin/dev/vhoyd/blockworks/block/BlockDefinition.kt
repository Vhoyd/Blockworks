package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.core.BlockBreakAction
import dev.vhoyd.blockworks.event.BlockInstanceBrokenEvent
import dev.vhoyd.blockworks.loot.ConditionalDrop
import dev.vhoyd.blockworks.loot.DeterminedDrop
import dev.vhoyd.blockworks.mining.Attributable
import dev.vhoyd.blockworks.mining.Attribute
import org.bukkit.Material
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player

/**
 * Objects of this class act as a blueprint for custom block behavior.
 * This acts like a behavior change towards a block with the matching material.
 * @property possibleDrops a `List<`[ConditionalDrop]`>` used to determine what could drop when a
 * block of this definition is broken. For multiple drops at once, use multiple `ConditionalDrop`s.
 * @property material the vanilla [Material] type of this block
 * @property drop a [ConditionalDrop] that could potentially drop when a block of this type is broken.
 * @property attributes a `Map<`[Attribute]`,Any>` that defines the baseline properties for any instance of this
 * block definition. Cannot be modified directly through the definition; instead modify the instance's attributes.
 * @property brokenMaterial the vanilla [Material] block type to replace this block type when broken
 * @property breakCondition the condition under which this block should be flagged as "broken". This is called
 * by [BlockInstance]s that use this definition, providing themselves as context during evaluation.
 * @property breakBehavior the [BlockBreakAction] to be triggered upon breaking a block of this type. Called
 * after its corresponding [BlockInstanceBrokenEvent] and before its [dropBehavior]
 * @property dropBehavior an optionally overridable behavior for what happens when this `BlockDefinition`
 * generates its reward(s). By default, this mimics vanilla behavior of dropping exp and items at block location;
 * this will be called after its corresponding [BlockInstanceBrokenEvent], and after its [breakBehavior]
 */
data class BlockDefinition(
    val material: Material,
    val possibleDrops: List<ConditionalDrop>,
    val attributes: Map<Attribute<*,*>, Any>,
    val breakCondition : (BlockInstance) -> Boolean,
    val brokenMaterial : Material = Material.AIR,
    val breakBehavior : BlockBreakAction = { },
    val dropBehavior : (DeterminedDrop, BlockInstance) -> Unit = { drop, instance ->

        val world = instance.location.world
        val location = instance.location.add(0.5, 0.5, 0.5)
        drop.exp.forEach {
            if (it > 0) {
                val orb = world.spawn(
                    location,
                    ExperienceOrb::class.java
                )
                orb.experience = it
            }
        }

        drop.splitDrops.forEach {
            world.dropItemNaturally(location, it)
        }


    }
) : Attributable {

    companion object {
        val VANILLA_BREAK_CONDITION : (BlockInstance) -> Boolean = { _ -> false}

        fun vanilla(material : Material) : BlockDefinition {
       return BlockDefinition(
                material,
                listOf(),
                attributes = emptyMap(),
                breakCondition = VANILLA_BREAK_CONDITION,
                breakBehavior = { instance : BlockInstance ->  instance.location.block.breakNaturally(instance.breaker.delegateAs<Player>().equipment.itemInMainHand)
                },
                dropBehavior = {_,_ ->  } ,
            )
        }
    }



    /**
     * Definitions cannot have their attributes modified. This should never be called.
     * @throws IllegalStateException why did you call it??
     */
    @Deprecated("Definitions cannot have their attributes modified. This should never be called.")
    override fun <P : Any, C : Any> setAttribute(
        attribute: Attribute<P, C>,
        value: C
    ) {
        error("Block definition attributes represent defaults and cannot be modified after creation.")
    }

    override fun <P : Any, C : Any> getAttribute(attribute: Attribute<P, C>): C {

        @Suppress("UNCHECKED_CAST")
        return attributes[attribute] as C
    }
}

