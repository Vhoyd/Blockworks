package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.core.BlockBreakAction
import dev.vhoyd.blockworks.event.BlockInstanceBrokenEvent
import dev.vhoyd.blockworks.loot.ConditionalDrop
import dev.vhoyd.blockworks.loot.DeterminedDrop
import dev.vhoyd.blockworks.model.Attributable
import dev.vhoyd.blockworks.model.Attribute
import dev.vhoyd.blockworks.model.BlockBreaker
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.generator.WorldInfo
import org.bukkit.util.BoundingBox

/**
 * Objects of this class act as a blueprint for custom block behavior.
 * This acts like a behavior change towards a block with the matching material.
 * @property material the vanilla [Material] type of this block.
 * @property region a `BoundingBox` outlining the coordinate region that this definition applies to. If left null,
 * this will apply to all blocks at any coordinate.
 * @property world a `WorldInfo` describing what world this definition's behavior applies to. If left null,
 * this will apply to all blocks in any world.
 * @property possibleDrops a `List<`[ConditionalDrop]`>` used to determine what could drop when a
 * block of this definition is broken. For multiple drops at once, use multiple `ConditionalDrop`s.
 * @property attributes a `Map<`[Attribute]`,Any>` that defines the baseline properties for any instance of this
 * block definition. Cannot be modified directly through the definition; instead modify the instance's attributes.
 * @property breakCondition the condition under which this block should be flagged as "broken". This is called
 * by [BlockInstance]s that use this definition, providing themselves as context during evaluation. If left null,
 * instances will assume the default break condition declared via [dev.vhoyd.blockworks.core.Config.defaultBreakCondition]
 * @property brokenMaterial the vanilla [Material] block type to replace this block type when broken. If left null,
 * instances will assume the default material declared via [dev.vhoyd.blockworks.core.Config.defaultReplacementMaterial]
 * @property breakBehavior the [BlockBreakAction] to be triggered upon breaking a block of this type. Called
 * after its corresponding [BlockInstanceBrokenEvent] and before its [dropBehavior]
 * @property dropBehavior an optionally overridable behavior for what happens when this `BlockDefinition`
 * generates its reward(s). By default, this mimics vanilla behavior of dropping exp and items at block location;
 * this will be called after its corresponding [BlockInstanceBrokenEvent], and after its [breakBehavior]. If left null,
 * instances will assume the default drop behavior declared via [dev.vhoyd.blockworks.core.Config]
 */
@ConsistentCopyVisibility
data class BlockDefinition private constructor(
    val material: Material,
    val region: BoundingBox?,
    val world: WorldInfo?,
    val possibleDrops: List<ConditionalDrop>,
    val attributes: Map<Attribute<*,*>, Any>,
    val breakCondition : ((BlockInstance) -> Boolean)?,
    val brokenMaterial : Material?,
    val breakBehavior : BlockBreakAction = { },
    val dropBehavior : ((DeterminedDrop, BlockInstance) -> Unit)?
) : Attributable {

    companion object {
        val VANILLA_BREAK_CONDITION : (BlockInstance) -> Boolean = { _ -> false}
        val DEFAULT_DROP_BEHAVIOR : (DeterminedDrop, BlockInstance) -> Unit = { drop, instance ->

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


        /**
         * Generates a "vanilla" block definition, defined as having no attributes, an always-false break condition
         * (this is instead handled by the client), a breakBehavior of breaking the block as normal gameplay would,
         * and no custom dropBehavior (handled by the `breakNaturally` method called in breakBehavior)
         */
        fun vanilla(
            material : Material,
            region: BoundingBox? = null,
            world: WorldInfo? = null
        ) : BlockDefinition {
       return BlockDefinition(
           material,
           region,
           world,
           listOf(),
           attributes = emptyMap(),
           breakCondition = VANILLA_BREAK_CONDITION,
           breakBehavior = { instance: BlockInstance ->
               instance.location.block.breakNaturally(instance.breaker.delegateAs<Player>().equipment.itemInMainHand)
           },
           dropBehavior = { _, _ -> },
           brokenMaterial = Material.AIR
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

    /**
     * Evaluates the data of the given `Block` against the settings of this `BlockDefinition` to evaluate whether
     * the `Material`, `WorldInfo.name`, and `Location` are valid.
     * @return `true` if the checks all pass, otherwise `false`.
     */
    fun isValidInstance(block : Block) : Boolean {
        if (block.blockData.material !== material) return false
        if (region != null && !region.contains(block.location.toVector())) return false
        if (world != null && block.world.name != world.name) return false
        return true
    }

    /**
     * Creates a new [BlockInstance] using data from the provided `Block`, so long as it meets the requirements
     * outlined in [isValidInstance].
     * @return the created [BlockInstance] if conditions are appropriate, otherwise `null`.
     */
    fun createInstance(block : Block, breaker: BlockBreaker<*>) : BlockInstance? {
        if (isValidInstance((block))) return BlockInstance(this, block.location, breaker)
        return null
    }

    class Builder(val material: Material) {

        private var region: BoundingBox? = null
        private var world: WorldInfo? = null

        private var possibleDrops: List<ConditionalDrop> = listOf()
        private var attributes: Map<Attribute<*,*>, Any> = emptyMap()
        private var breakCondition : ((BlockInstance) -> Boolean)? = null
        private var brokenMaterial : Material? = null
        private var breakBehavior : BlockBreakAction = { }
        private var dropBehavior : ((DeterminedDrop, BlockInstance) -> Unit)? = null

        fun region(region: BoundingBox?) = apply { this.region = region }
        fun world(world: WorldInfo?) = apply { this.world = world }
        fun possibleDrops(drops: List<ConditionalDrop>) = apply { this.possibleDrops = drops }
        fun attributes(attributes: Map<Attribute<*,*>, Any>) = apply { this.attributes = attributes }
        fun breakCondition(breakCondition : (BlockInstance) -> Boolean) = apply { this.breakCondition = breakCondition}
        fun brokenMaterial(material : Material) = apply { this.brokenMaterial = material }
        fun breakBehavior(breakBehavior : BlockBreakAction) = apply { this.breakBehavior = breakBehavior}
        fun dropBehavior(dropBehavior : (DeterminedDrop, BlockInstance) -> Unit) = apply {this.dropBehavior = dropBehavior}
        fun build() : BlockDefinition {
            return BlockDefinition(material,region, world, possibleDrops, attributes, breakCondition, brokenMaterial, breakBehavior, dropBehavior)
        }
    }

}

