package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.loot.ConditionalDrop
import org.bukkit.Material

typealias WeightedEntry<T> = Pair<T, Int>

/**
 * Objects of this class act as a blueprint for custom block behavior.
 * This acts like a behavior change towards a block with the matching material.
 * @property possibleDrops a `List` of [ConditionalDrop]s used to determine what could drop when a
 * block of this definition is broken. For multiple drops at once, use multiple `ConditionalDrop`s.
 */
class BlockDefinition {

    val material : Material
    val possibleDrops : List<ConditionalDrop>
    val breakingPower : Int
    val blockStrength : Int
    val brokenMaterial : Material
    val brokenAction : BlockBreakAction

    /**
     * @param material the vanilla [Material] type of this block
     * @param possibleDrops a `List` of [ConditionalDrop]s that could potentially drop when a block of this definition is broken.
     * @param brokenMaterial the vanilla [Material] block type to replace this block type when broken
     * @param breakingPower the minimum breaking power required to break this block
     * @param blockStrength how hard it is to break this block, in arbitrary units (scaled by [dev.vhoyd.blockworks.core.Config] if enabled!)
     * @param blockBreakAction the [BlockBreakAction] to be triggered upon breaking this block
     */
    constructor(material: Material, possibleDrops: List<ConditionalDrop>, breakingPower: Int, blockStrength: Int, blockBreakAction: BlockBreakAction, brokenMaterial: Material = Material.AIR ) {
        this.material = material
        this.brokenMaterial = brokenMaterial
        this.possibleDrops = possibleDrops
        this.breakingPower = breakingPower
        this.blockStrength = blockStrength
        this.brokenAction = blockBreakAction

    }

    /**
     * @param material the vanilla [Material] type of this block
     * @param drop  a [ConditionalDrop] that could potentially drop when a block of this definition is broken.
     * @param brokenMaterial the vanilla [Material] block type to replace this block type when broken
     * @param breakingPower the minimum breaking power required to break this block
     * @param blockStrength how hard it is to break this block, in arbitrary units (scaled by [dev.vhoyd.blockworks.core.Config] if enabled!)
     * @param blockBreakAction the [BlockBreakAction] to be triggered upon breaking this block
     */
    constructor(material: Material, drop: ConditionalDrop, breakingPower: Int, blockStrength: Int, blockBreakAction: BlockBreakAction, brokenMaterial: Material = Material.AIR ) :
        this(material, listOf(drop), breakingPower, blockStrength, blockBreakAction, brokenMaterial)
}

