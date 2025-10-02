package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.loot.ConditionalDrop
import dev.vhoyd.blockworks.mining.BlockworksAttributable
import dev.vhoyd.blockworks.mining.MiningAttribute
import dev.vhoyd.blockworks.util.EmptyValue
import org.bukkit.Material

typealias WeightedEntry<T> = Pair<T, Int>

/**
 * Objects of this class act as a blueprint for custom block behavior.
 * This acts like a behavior change towards a block with the matching material.
 * @property possibleDrops a `List<`[ConditionalDrop]`>` used to determine what could drop when a
 * block of this definition is broken. For multiple drops at once, use multiple `ConditionalDrop`s.
 */
class BlockDefinition : BlockworksAttributable {

    val material : Material
    val possibleDrops : List<ConditionalDrop>
    val brokenMaterial : Material
    val brokenAction : BlockBreakAction
    val attributeMap : Map<MiningAttribute<*,*>, Any>
    val breakCondition : (BlockInstance) -> Boolean

    /**
     * @param material the vanilla [Material] type of this block
     * @param possibleDrops a `List<`[ConditionalDrop]`>` that could potentially drop when a block of this type is broken.
     * @param attributes a `Map<`[MiningAttribute]`,Any>` that defines the baseline properties for any instance of this
     * block definition. Cannot be modified directly through the definition; instead modify the instance's attributes.
     * @param brokenMaterial the vanilla [Material] block type to replace this block type when broken
     * @param breakCondition the condition under which this block should be flagged as "broken". This is called
     * by [BlockInstance]s that use this definition, providing themselves as context during evaluation.
     * @param blockBreakAction the [BlockBreakAction] to be triggered upon breaking a block of this type.
     */
    constructor(material: Material, possibleDrops: List<ConditionalDrop>, attributes: Map<MiningAttribute<*,*>, Any>, breakCondition : (BlockInstance) -> Boolean, blockBreakAction: BlockBreakAction = EmptyValue.BLOCKBREAKACTION, brokenMaterial: Material = Material.AIR ) {
        this.material = material
        this.brokenMaterial = brokenMaterial
        this.attributeMap = attributes
        this.possibleDrops = possibleDrops
        this.brokenAction = blockBreakAction
        this.breakCondition = breakCondition
    }

    /**
     * @param material the vanilla [Material] type of this block
     * @param drop a [ConditionalDrop] that could potentially drop when a block of this type is broken.
     * @param attributes a `Map<`[MiningAttribute]`,Any>` that defines the baseline properties for any instance of this
     * block definition. Cannot be modified directly through the definition; instead modify the instance's attributes.
     * @param brokenMaterial the vanilla [Material] block type to replace this block type when broken
     * @param breakCondition the condition under which this block should be flagged as "broken". This is called
     * by [BlockInstance]s that use this definition, providing themselves as context during evaluation.
     * @param blockBreakAction the [BlockBreakAction] to be triggered upon breaking a block of this type.
     */
    constructor(material: Material, drop: ConditionalDrop, attributes: Map<MiningAttribute<*,*>, Any> = mutableMapOf(), breakCondition : (BlockInstance) -> Boolean, blockBreakAction: BlockBreakAction = EmptyValue.BLOCKBREAKACTION, brokenMaterial: Material = Material.AIR ) :
            this(material, listOf(drop), attributes, breakCondition, blockBreakAction, brokenMaterial)

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
        return attributeMap[miningAttribute] as C
    }
}

