package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.core.BlockBreakAction
import dev.vhoyd.blockworks.loot.ConditionalDrop
import dev.vhoyd.blockworks.mining.BlockworksAttributable
import dev.vhoyd.blockworks.mining.MiningAttribute
import org.bukkit.Material

/**
 * Objects of this class act as a blueprint for custom block behavior.
 * This acts like a behavior change towards a block with the matching material.
 * @property possibleDrops a `List<`[ConditionalDrop]`>` used to determine what could drop when a
 * block of this definition is broken. For multiple drops at once, use multiple `ConditionalDrop`s.
 * @property material the vanilla [Material] type of this block
 * @property drop a [ConditionalDrop] that could potentially drop when a block of this type is broken.
 * @property attributes a `Map<`[MiningAttribute]`,Any>` that defines the baseline properties for any instance of this
 * block definition. Cannot be modified directly through the definition; instead modify the instance's attributes.
 * @property brokenMaterial the vanilla [Material] block type to replace this block type when broken
 * @property breakCondition the condition under which this block should be flagged as "broken". This is called
 * by [BlockInstance]s that use this definition, providing themselves as context during evaluation.
 * @property onBreak the [BlockBreakAction] to be triggered upon breaking a block of this type.
 */
data class BlockDefinition(
    val material: Material,
    val possibleDrops: List<ConditionalDrop>,
    val attributes: Map<MiningAttribute<*,*>, Any>,
    val breakCondition : (BlockInstance) -> Boolean,
    val onBreak : BlockBreakAction = { },
    val brokenMaterial: Material = Material.AIR
) : BlockworksAttributable {
    /**
     * Definitions cannot have their attributes modified. This should never be called.
     * @throws IllegalStateException why did you call it??
     */
    @Deprecated("Definitions cannot have their attributes modified. This should never be called.")
    override fun <P : Any, C : Any> setAttribute(
        miningAttribute: MiningAttribute<P, C>,
        value: C
    ) {
        error("Block definition attributes cannot be modified after creation.")
    }

    override fun <P : Any, C : Any> getAttribute(miningAttribute: MiningAttribute<P, C>): C {

        @Suppress("UNCHECKED_CAST")
        return attributes[miningAttribute] as C
    }
}

