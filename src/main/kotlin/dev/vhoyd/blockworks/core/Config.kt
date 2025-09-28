package dev.vhoyd.blockworks.core

import dev.vhoyd.blockworks.block.BlockDataLoader
import dev.vhoyd.blockworks.block.BlockDefinition
import org.bukkit.Material

/**
 * Class for changing parts of the plugin's function.
 * When not specified, all booleans default to false. All numeric scalar values default to 1.
 * NOTE: config data is not fully implemented. More robust config analysis will be implemented in future versions.
 * @property blockDataLoader a [BlockDataLoader] with a defined [BlockDataLoader.loadAll] method
 * for creating a `List` of [BlockDefinition]s.
 * @property dynamicFortuneScaling whether this instance should scale drops dynamically with fortune. The average
 * reward amount is the same either way; this modifies the exact behavior of how bonus drops are applied.
 *
 * If `true`, remaining fortune is applied relative to the amount of base drops.
 * At 70 fortune and 3 drops, this maths out to `(70/100) * 3 = 2.1`, or +2 drops with a 10% chance for a bonus drop.
 *
 * If `false`, fortune is applied as a raw % odds for a bonus drop. Using 70 fortune and 3 drops again,
 * 70% of the time this would be +3 drops and 30% of the time it would be +0 drops.
 * @see dev.vhoyd.blockworks.internal.task.BlockBreakTick.calculateDropAmount
 */
class Config {
    val blockDataLoader : BlockDataLoader
    val ignoreBreakingPower : Boolean
    val ignoreMiningFortune : Boolean
    val dynamicFortuneScaling : Boolean
    val miningRateScale : Double
    val blockList : List<BlockDefinition>
    val materialList : List<Material>

    /**
     * @param ignoreBreakingPower whether this instance of the library should ignore
     * all checks related to minimum breaking power requirements.
     * @param ignoreMiningFortune whether this instance of the library should ignore scaled
     * drop outputs based on a mining fortune stat.
     * @param dynamicFortuneScaling whether this instance should scale drops dynamically with fortune.
     * @param blockStrengthScale treated as the numerator in a fraction of `blockStrengthScale / miningSpeedScale`
     * @param miningSpeedScale treated as the denominator in a fraction of `blockStrengthScale / miningSpeedScale`
     *
     * Do note that the aforementioned ratio is computed during instantiation, and cannot be changed later.
     * This ratio is used as a scalar value for how quickly blocks are broken. The higher the scalar, the
     * longer blocks take to break.
     */

    constructor(
        blockDataLoader : BlockDataLoader,
        ignoreBreakingPower : Boolean = false,
        ignoreMiningFortune : Boolean = false,
        dynamicFortuneScaling : Boolean = false,
        blockStrengthScale : Double = 1.0,
        miningSpeedScale : Double = 1.0
    ) : this(
        blockDataLoader,
        ignoreBreakingPower,
        ignoreMiningFortune,
        dynamicFortuneScaling,
        blockStrengthScale / miningSpeedScale
    )

    /**
     * @param ignoreBreakingPower whether this instance of the library should ignore
     * all checks related to minimum breaking power requirements.
     * @param ignoreMiningFortune whether this instance of the library should ignore scaled
     * drop outputs based on a mining fortune stat.
     * @param dynamicFortuneScaling whether this instance should scale drops dynamically with fortune.
     * @param miningRateScale the scalar value for how quickly blocks are broken. The higher the scalar, the
     * longer blocks take to break.
     */

    constructor(
        blockDataLoader : BlockDataLoader,
        ignoreBreakingPower : Boolean = false,
        ignoreMiningFortune : Boolean = false,
        dynamicFortuneScaling : Boolean = false,
        miningRateScale : Double = 1.0
    ) {
        this.blockDataLoader = blockDataLoader
        blockList = blockDataLoader.loadAll()
        materialList = blockList.map { it.material }
        this.ignoreBreakingPower = ignoreBreakingPower
        this.ignoreMiningFortune = ignoreMiningFortune
        this.dynamicFortuneScaling = dynamicFortuneScaling
        this.miningRateScale = miningRateScale
    }





}