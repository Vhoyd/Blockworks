package dev.vhoyd.blockworks.block

import dev.vhoyd.blockworks.event.BlockInstanceBrokenEvent
import dev.vhoyd.blockworks.loot.ConditionalDrop
import dev.vhoyd.blockworks.loot.DeterminedDrop
import dev.vhoyd.blockworks.model.Attributable
import dev.vhoyd.blockworks.model.Attribute
import dev.vhoyd.blockworks.model.BlockBreaker
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.Block
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * Objects of this class act as a blueprint for custom block behavior.
 * This acts like a behavior change towards a block with the matching material and conditions.
 * @property requirements the conditions under which a valid [BlockInstance] is allowed, provided context of its `Block`
 * and the `BlockBreaker` attempting to mine it.
 * @property drops a `List<`[ConditionalDrop]`>` used to determine what could drop when a
 * block of this definition is broken. For multiple drops at once, use multiple `ConditionalDrop`s.
 * @property attributes a `Map<`[Attribute]`,Any>` that defines the baseline properties for any instance of this
 * block definition. Cannot be modified directly through the definition; instead modify the instance's attributes.
 * @property breakCondition the condition under which this block should be flagged as "broken". This is called
 * by [BlockInstance]s that use this definition, providing themselves as context during evaluation. If left null,
 * instances will assume the default break condition declared via [dev.vhoyd.blockworks.core.Config.defaultBreakCondition]
 * @property replacementMaterial the vanilla [Material] block type to replace this block type when broken. If left null,
 * instances will assume the default material declared via [dev.vhoyd.blockworks.core.Config.defaultReplacementMaterial]
 * @property breakBehavior behavior to be called upon breaking a block of this type. Called
 * after its corresponding [BlockInstanceBrokenEvent] and before its [dropBehavior]
 * @property dropBehavior an optionally overridable behavior for what happens when this `BlockDefinition`
 * generates its reward(s). By default, this mimics vanilla behavior of dropping exp and items at block location;
 * this will be called after its corresponding [BlockInstanceBrokenEvent], and after its [breakBehavior]. If left null,
 * instances will assume the default drop behavior declared via [dev.vhoyd.blockworks.core.Config]
 */
@ConsistentCopyVisibility
data class BlockDefinition private constructor(
    val requirements : BiPredicate<Block, BlockBreaker<*>>,
    val drops: List<ConditionalDrop>,
    val attributes: Map<Attribute<*,*>, Any>,
    val breakCondition : Predicate<BlockInstance>?,
    val replacementMaterial : Material?,
    val breakBehavior : Consumer<BlockInstance>,
    val dropBehavior : Consumer<DeterminedDrop>?,
    val breakSound : Sound,
) : Attributable {

    companion object {
        private val airBreak = Material.AIR.createBlockData().soundGroup.breakSound
        private val emptyBreakConsumer = Consumer<BlockInstance> { }

        @JvmStatic
        val VANILLA_BREAK_CONDITION = Predicate<BlockInstance> { false }

        @JvmStatic
        val DEFAULT_DROP_BEHAVIOR : Consumer<DeterminedDrop> = Consumer { drop ->

            val world = drop.blockInstance.location.world
            val location = drop.blockInstance.location.add(0.5, 0.5, 0.5)
            drop.exp.forEach {
                if (it > 0) {
                    val orb = world.spawn(
                        location,
                        ExperienceOrb::class.java
                    )
                    orb.experience = it

                    // using Math.random() in this day and age? How exploitable! Someone could predict the
                    // path of the experience orbs spawned...
                    val pitch = Math.random() * 2 * Math.PI
                    val yaw = Math.random() * 2 * Math.PI
                    val roll = Math.random() * 2 * Math.PI

                    // velocity is in blocks/sec so this throws it ~1.5 blocks in tha direction accounting for friction
                    val vector = Vector(pitch,yaw, roll).normalize().multiply(0.1)


                    orb.velocity = vector
                }
            }

            drop.splitDrops.forEach {
                world.dropItemNaturally(location, it.clone())
            }


        }


        /**
         * Generates a "vanilla" block definition, defined as having no attributes, an always-false break condition
         * (this is instead handled by the client), a breakBehavior of breaking the block as normal gameplay would,
         * and no custom dropBehavior (handled by the `breakNaturally` method called in breakBehavior)
         */
        @JvmStatic
        fun vanilla(
            requirements: BiPredicate<Block, BlockBreaker<*>>,
        ) : BlockDefinition {
       return BlockDefinition(
           requirements,
           listOf(),
           attributes = emptyMap(),
           breakCondition = VANILLA_BREAK_CONDITION,
           breakBehavior = { instance ->
               instance.location.block.breakNaturally(instance.breaker.delegateAs<Player>().equipment.itemInMainHand)
           },
           dropBehavior = { _ -> },
           replacementMaterial = Material.AIR,
           breakSound = airBreak
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
     * Evaluates the data of the given `Block` and [BlockBreaker] against the settings of this `BlockDefinition` to
     * evaluate whether the conditions are valid, as determined by the behavior assigned to [requirements]
     * @return `true` if the internal `BiPredicate` assigned at construction passes, otherwise `false`.
     */
    fun isValidInstance(block : Block, breaker: BlockBreaker<*>) = requirements.test(block, breaker)

    /**
     * Creates a new [BlockInstance] using data from the provided `Block`, so long as it meets the requirements
     * outlined in [isValidInstance].
     * @return the created [BlockInstance] if conditions are appropriate, otherwise `null`.
     */
    fun createInstance(block : Block, breaker: BlockBreaker<*>) : BlockInstance? {
        if (requirements.test(block, breaker)) return BlockInstance(this, block.location, breaker)
        return null
    }

    @Suppress("Unused")
    class Builder(private val requirements : BiPredicate<Block, BlockBreaker<*>>) {


        private var possibleDrops: List<ConditionalDrop> = listOf()
        private var attributes: Map<Attribute<*,*>, Any> = emptyMap()
        private var breakCondition : Predicate<BlockInstance>? = null
        private var brokenMaterial : Material? = null
        private var breakBehavior : Consumer<BlockInstance> = emptyBreakConsumer
        private var dropBehavior : Consumer<DeterminedDrop>? = null
        private var breakSound : Sound = airBreak

        fun drops(drops: List<ConditionalDrop>) = apply { this.possibleDrops = drops }
        fun attributes(attributes: Map<Attribute<*,*>, Any>) = apply { this.attributes = attributes }
        fun breakCondition(breakCondition : Predicate<BlockInstance>) = apply { this.breakCondition = breakCondition}
        fun replacementMaterial(material : Material) = apply { this.brokenMaterial = material }
        fun breakBehavior(breakBehavior : Consumer<BlockInstance>) = apply { this.breakBehavior = breakBehavior}
        fun dropBehavior(dropBehavior : Consumer<DeterminedDrop>) = apply {this.dropBehavior = dropBehavior}
        fun breakSound(sound : Sound) = apply { this.breakSound = sound }
        fun build() : BlockDefinition {
            return BlockDefinition(requirements, possibleDrops, attributes, breakCondition, brokenMaterial, breakBehavior, dropBehavior, breakSound)
        }
    }


}

