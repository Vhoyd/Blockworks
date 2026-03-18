package dev.vhoyd.blockworks.api.block

import dev.vhoyd.blockworks.api.core.Blockworks
import dev.vhoyd.blockworks.api.event.BlockInstanceBrokenEvent
import dev.vhoyd.blockworks.api.loot.ConditionalDrop
import dev.vhoyd.blockworks.api.loot.DeterminedDrop
import dev.vhoyd.blockworks.api.model.Attributable
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.api.model.delegateAs
import dev.vhoyd.blockworks.internal.InternalAttribute
import dev.vhoyd.blockworks.internal.InternalBLockDefinition
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.ExperienceOrb
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.collections.listOf

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
 * instances will assume the default break condition declared via [dev.vhoyd.blockworks.api.core.Config.defaultBreakCondition]
 * @property replacement the vanilla [Material] block type to replace this block type when broken. If left null,
 * instances will assume the default material declared via [dev.vhoyd.blockworks.api.core.Config.defaultReplacementMaterial]
 * @property onBreak behavior to be called upon breaking a block of this type. Called
 * after its corresponding [BlockInstanceBrokenEvent] and before its [onDrop]
 * @property onDrop an optionally overridable behavior for what happens when this `BlockDefinition`
 * generates its reward(s). By default, this mimics vanilla behavior of dropping exp and items at block location;
 * this will be called after its corresponding [BlockInstanceBrokenEvent], and after its [onBreak]. If left null,
 * instances will assume the default drop behavior declared via [dev.vhoyd.blockworks.api.core.Config]
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

    companion object {
        private val emptyBreakConsumer : Consumer<BlockInstance> = Consumer { }

        internal val vanillaDmg : Attribute<Float, Float> = InternalAttribute("internal-dmg", PersistentDataType.FLOAT)
        internal val vanillaHaste : Attribute<Byte, Boolean> = InternalAttribute("internal-haste", PersistentDataType.BOOLEAN)
        internal val vanillaFatigue : Attribute<Byte, Boolean> = InternalAttribute("internal-fatigue", PersistentDataType.BOOLEAN)

        /**
         * A Predicate that always returns false, since the server should not be the one doing the breaking
         * for completely vanilla blocks.
         */
        @JvmStatic
        val vanillaBreakPredicate : Predicate<BlockInstance> = Predicate { false }

        /**
         * Spawns every rolled drop and experience orb at the center of the block, with some randomized velocity.
         */
        @JvmStatic
        val defaultDropBehavior : Consumer<DeterminedDrop> = Consumer { drop ->

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
                    val vector = Vector(pitch,yaw, roll).normalize().multiply(0.2)


                    orb.velocity = vector
                }
            }

            drop.splitDrops.forEach {
                world.dropItemNaturally(location, it.clone())
            }


        }


        /**
         * Generates a "vanilla" block definition, defined as having no user-provided attributes, breaking speed
         * determined by the client, break behavior of [Block.breakNaturally] given the held ItemStack,
         * and no custom drop behavior.
         */
        @JvmStatic
        fun vanilla(
            requirements: BiPredicate<Block, BlockBreaker<*>>,
            ignoreFatigue: Boolean,
            ignoreHaste: Boolean,
        ) : BlockDefinition {
       return InternalBLockDefinition(
           requirements,
           listOf(),
           attributes = mutableMapOf(
               vanillaDmg to 0f,
               vanillaHaste to ignoreHaste,
               vanillaFatigue to ignoreFatigue,
           ),
           breakIf = { it[vanillaDmg]!! >= 1f },
           onBreak = { instance ->
               val player = instance.breaker.delegateAs<Player>()!!
               instance.location.block.breakNaturally(player.equipment.itemInMainHand)
               player.sendBlockDamage(instance.location, 0f, -player.entityId)
           },
           onDrop = { _ -> },
           replacement = Material.AIR,
           onTick = { _ -> },
       )
        }
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
     * @return the created [BlockInstance] if conditions are appropriate, otherwise `null`.
     */
    fun createInstance(block : Block, breaker: BlockBreaker<*>) : BlockInstance


    /**
     * DefaultImplBuilder class for creating a default BlockDefinition object.
     */
    @Suppress("Unused") // for external use only
    class DefaultImplBuilder(blockworks: Blockworks, private val requirements : BiPredicate<Block, BlockBreaker<*>>) {


        private var drops: Iterable<ConditionalDrop> = listOf()
        private var attributes: MutableMap<Attribute<*,*>, Any> = mutableMapOf()
        private var breakCondition : Predicate<BlockInstance> = blockworks.config.defaultBreakCondition
        private var replacement : Material = blockworks.config.defaultReplacementMaterial
        private var breakBehavior : Consumer<BlockInstance> = emptyBreakConsumer
        private var dropBehavior : Consumer<DeterminedDrop> = blockworks.config.defaultDropBehavior
        private var tickBehavior : Consumer<BlockInstance> = Consumer {}

        infix fun withDrops(drops: Iterable<ConditionalDrop>) : DefaultImplBuilder = apply { this.drops = drops }
        infix fun withAttributes(attributes: MutableMap<Attribute<*,*>, Any>) : DefaultImplBuilder = apply { this.attributes = attributes }
        infix fun breakIf(breakCondition : Predicate<BlockInstance>) : DefaultImplBuilder = apply { this.breakCondition = breakCondition}
        infix fun replacedBy(material : Material) : DefaultImplBuilder = apply { this.replacement = material }
        infix fun whenBroken(breakBehavior : Consumer<BlockInstance>): DefaultImplBuilder = apply { this.breakBehavior = breakBehavior}
        infix fun whenReward(dropBehavior : Consumer<DeterminedDrop>) : DefaultImplBuilder = apply {this.dropBehavior = dropBehavior}
        infix fun whenTicked(tickBehavior : Consumer<BlockInstance>) : DefaultImplBuilder = apply { this.tickBehavior = tickBehavior}

        fun build() : BlockDefinition = InternalBLockDefinition(
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

