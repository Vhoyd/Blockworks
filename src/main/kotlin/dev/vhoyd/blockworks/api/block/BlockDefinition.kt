package dev.vhoyd.blockworks.api.block

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.api.event.BlockInstanceBrokenEvent
import dev.vhoyd.blockworks.api.loot.ConditionalDrop
import dev.vhoyd.blockworks.api.loot.DeterminedDrop
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.impl.block.ProvidedBlockDefinition
import org.bukkit.Material
import org.bukkit.block.Block
import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.function.Predicate

/**
 * Objects of this class act as a blueprint for custom block behavior.
 * This acts like a behavior change towards a block with the matching material and conditions.
 *
 * @property requirements the conditions under which a valid [BlockInstance] is allowed, provided context of its `Block`
 * and the `BlockBreaker` attempting to mine it.
 * @property drops a `List<`[ConditionalDrop]`>` used to determine what could drop when a
 * block of this definition is broken. For multiple drops at once, use multiple `ConditionalDrop`s.
 * @property attributes a `Map<`[Attribute]`,Any>` that defines the baseline properties for any instance of this
 * block definition. Cannot be modified directly through the definition; instead modify the instance's attributes.
 * @property breakIf the condition under which this block should be flagged as "broken". This is called
 * by [BlockInstance]s that use this definition, providing themselves as context during evaluation. If left null,
 * instances will assume the default break condition declared via [Blockworks.blockDefinitionBehavior]
 * @property replacement the vanilla [Material] block type to replace this block type when broken. If left null,
 * instances will assume the default material declared via [Blockworks.blockDefinitionBehavior]
 * @property onBreak behavior to be called upon breaking a block of this type. Called
 * after its corresponding [BlockInstanceBrokenEvent] and before its [onDrop]
 * @property onDrop an optionally overridable behavior for what happens when this `BlockDefinition`
 * generates its reward(s). By default, this mimics vanilla behavior of dropping exp and items at block location;
 * this will be called after its corresponding [BlockInstanceBrokenEvent], and after its [onBreak]. If left null,
 * instances will assume the default drop behavior declared via [Blockworks.blockDefinitionBehavior]
 * @property onTick behavior to be called each tick that this block is being broken.
 */

interface BlockDefinition : Attributable {

    val requirements : BiPredicate<Block, BlockBreaker<*>>
    val drops: Iterable<ConditionalDrop>
    override val attributes: MutableMap<Attribute<*,*>, Any>
    val breakIf : Predicate<BlockInstance>
    val replacement : Material
    val onTick : Consumer<BlockInstance>
    val onBreak : Consumer<BlockInstance>
    val onDrop : Consumer<DeterminedDrop>


    /**
     * Creates a new [BlockInstance] using data from the provided `Block` and `BlockBreaker`, without validating
     * whether the requirements are met to create one with that context.
     */
    fun createInstance(block : Block, breaker: BlockBreaker<*>) : BlockInstance



    companion object {

        val EMPTY_BEHAVIOR : BlockDefinition = ProvidedBlockDefinition(
            requirements = { _ , _ -> true },
            drops = listOf(),
            attributes = mutableMapOf(),
            breakIf = { false },
            replacement = Material.AIR,
            onTick = {   },
            onBreak = { },
            onDrop = {   }
        )

        /**
         * Spawns every rolled drop and experience orb at the center of the block, with some randomized velocity.
         */
        //TODO figure out of this serves a purpose here or if it's just better off in my own test implementation
//        @JvmStatic
//        val defaultDropBehavior : Consumer<DeterminedDrop> = Consumer { drop ->
//
//            val world = drop.block.location.world
//            val location = drop.block.location.add(0.5, 0.5, 0.5)
//            drop.exp.forEach {
//                if (it > 0) {
//                    val orb = world.spawn(
//                        location,
//                        ExperienceOrb::class.java
//                    )
//                    orb.experience = it
//
//                    // using Math.random() in this day and age? How exploitable! Someone could predict the
//                    // path of the experience orbs spawned...
//                    val pitch = Math.random() * 2 * Math.PI
//                    val yaw = Math.random() * 2 * Math.PI
//                    val roll = Math.random() * 2 * Math.PI
//
//                    // velocity is in blocks/sec so this throws it ~1.5 blocks in the direction accounting for friction
//                    val vector = Vector(pitch,yaw, roll).normalize().multiply(0.2)
//
//
//                    orb.velocity = vector
//                }
//            }
//
//            drop.splitDrops.forEach {
//                world.dropItemNaturally(location, it.clone())
//            }
//
//
//        }


        /**
         * Generates a "vanilla" block definition.
         *
         * This is defined as having no user-provided attributes, breaking speed
         * determined by the client, break behavior of [Block.breakNaturally] given the held ItemStack,
         * and no custom drop behavior. Please note that these should only be broken by `BlockBreaker<Player>`s.
         */

        //TODO figure out how to make this more flexible before offering it as a method
//        @JvmStatic
//        @Suppress("unused") // for external use only
//        fun vanilla(
//            requirements: BiPredicate<Block, BlockBreaker<*>>,
//            ignoreFatigue: Boolean,
//            ignoreHaste: Boolean,
//        ) : BlockDefinition {
//            return VanillaDefinition(requirements, ignoreHaste, ignoreFatigue)
//        }
    }

    /**
     * Definitions should not have their attributes modified since they are default states.
     * This should never be called nor overridden.
     * @throws IllegalStateException when called.
     */
    @Deprecated("Definitions should not have their attributes modified. This should never be called.")
    override fun <P : Any, C : Any> setAttribute(
        attribute: Attribute<P, C>,
        value: C
    ) {
        error("Block definition attributes represent defaults and should not be modified after creation.")
    }

    /**
     * Evaluates the data of the given `Block` and [BlockBreaker]. By default, this looks at [requirements] to
     * determine validity.
     * @return `true` if a new `BlockInstance` is allowed, otherwise false.
     */
    fun isValidInstance(block : Block, breaker: BlockBreaker<*>) : Boolean = requirements.test(block, breaker)


    /**
     * Creates a new [BlockInstance] using data from the provided `Block`, so long as it meets the requirements
     * outlined in [isValidInstance].
     * Default implementation returns an appropriate [BlockInstance] or null, based on the return value of [isValidInstance].
     * @return the created [BlockInstance] if conditions are appropriate, otherwise `null`.
     */
    @Suppress("Unused") // for external use only
    fun createValidatedInstance(block : Block, breaker: BlockBreaker<*>) : BlockInstance? {
        return if (isValidInstance(block, breaker)) createInstance(block, breaker) else null
    }

    /**
     * Default builder class for creating a default BlockDefinition object.
     */
    @Suppress("Unused") // for external use only
    class DefaultDefinitionBuilder(blockworks: Blockworks, private val requirements : BiPredicate<Block, BlockBreaker<*>>) {

        private val default = blockworks.blockDefinitionBehavior

        private var drops: Iterable<ConditionalDrop> = default.drops
        private var attributes: MutableMap<Attribute<*,*>, Any> = default.attributes
        private var breakCondition : Predicate<BlockInstance> = default.breakIf
        private var replacement : Material = default.replacement
        private var breakBehavior : Consumer<BlockInstance> = default.onBreak
        private var dropBehavior : Consumer<DeterminedDrop> = default.onDrop
        private var tickBehavior : Consumer<BlockInstance> = default.onTick

        infix fun withDrops(drops: Iterable<ConditionalDrop>) : DefaultDefinitionBuilder = apply { this.drops = drops }
        infix fun withAttributes(attributes: Map<Attribute<*,*>, Any>) : DefaultDefinitionBuilder = apply { this.attributes = attributes.toMutableMap() }
        infix fun breakIf(breakCondition : Predicate<BlockInstance>) : DefaultDefinitionBuilder = apply { this.breakCondition = breakCondition}
        infix fun replacedBy(material : Material) : DefaultDefinitionBuilder = apply { this.replacement = material }
        infix fun whenBroken(breakBehavior : Consumer<BlockInstance>): DefaultDefinitionBuilder = apply { this.breakBehavior = breakBehavior}
        infix fun whenReward(dropBehavior : Consumer<DeterminedDrop>) : DefaultDefinitionBuilder = apply {this.dropBehavior = dropBehavior}
        infix fun whenTicked(tickBehavior : Consumer<BlockInstance>) : DefaultDefinitionBuilder = apply { this.tickBehavior = tickBehavior}


        fun build() : BlockDefinition = ProvidedBlockDefinition(
                requirements,
                drops,
                attributes,
                breakCondition,
                replacement,
                tickBehavior,
                breakBehavior,
                dropBehavior,
                )

    }


}

