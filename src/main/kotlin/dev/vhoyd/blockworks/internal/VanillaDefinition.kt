package dev.vhoyd.blockworks.internal

import dev.vhoyd.blockworks.api.block.BlockDefinition
import dev.vhoyd.blockworks.api.block.BlockInstance
import dev.vhoyd.blockworks.api.loot.ConditionalDrop
import dev.vhoyd.blockworks.api.loot.DeterminedDrop
import dev.vhoyd.blockworks.api.model.Attribute
import dev.vhoyd.blockworks.api.model.BlockBreaker
import dev.vhoyd.blockworks.api.model.delegateAs
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffectType
import java.util.function.BiPredicate
import java.util.function.Consumer
import java.util.function.Predicate
import kotlin.math.pow

internal class VanillaDefinition(
    override val requirements: BiPredicate<Block, BlockBreaker<*>>,
    ignoreHaste : Boolean,
    ignoreFatigue: Boolean,

) : BlockDefinition {

    override val drops: Iterable<ConditionalDrop> = listOf()
    override val attributes: MutableMap<Attribute<*, *>, Any> = mutableMapOf(
        vanillaDmg to 0f,
        vanillaHaste to ignoreHaste,
        vanillaFatigue to ignoreFatigue,
    )
    override val breakIf: Predicate<BlockInstance> = { it[vanillaDmg]!! >= 1f }
    override val onBreak : Consumer<BlockInstance> = { instance ->
        val player = instance.breaker.delegateAs<Player>()!!
        instance.location.block.breakNaturally(player.equipment.itemInMainHand)
        player.sendBlockDamage(instance.location, 0f, -player.entityId)
    }
    override val onDrop : Consumer<DeterminedDrop> = { _ -> }
    override val replacement = Material.AIR
    override val onTick : Consumer<BlockInstance> = {

        // TODO: figure out why this doesn't scale properly
        val player = it.breaker.delegateAs<Player>()!!
        val hasteEffect = player.getPotionEffect(PotionEffectType.HASTE)?.amplifier?.let { it + 1} ?: 0
        val fatigueEffect = player.getPotionEffect(PotionEffectType.MINING_FATIGUE)?.amplifier?.let { it + 1} ?: 0
        val hasteMult = if (!it[vanillaHaste]!!) 0.2f * hasteEffect + 1  else 1
        val fatigueMult = if (!it[vanillaFatigue]!!) 0.3.pow(
            fatigueEffect.coerceAtMost(4).toDouble()
        ) else 1
        val dmg = (it.location.block.getBreakSpeed(player) / hasteMult.toFloat()) / fatigueMult.toFloat()
        val total = it[vanillaDmg]!! + dmg * 10f
        player.sendBlockDamage(it.location, total.coerceAtMost(1f), -player.entityId)
        it[vanillaDmg] = total

    }


    companion object {
        private val vanillaDmg : Attribute<Float, Float> = InternalAttribute("internal-dmg", PersistentDataType.FLOAT)
        private val vanillaHaste : Attribute<Byte, Boolean> = InternalAttribute("internal-haste", PersistentDataType.BOOLEAN)
        private val vanillaFatigue : Attribute<Byte, Boolean> = InternalAttribute("internal-fatigue", PersistentDataType.BOOLEAN)
    }

}