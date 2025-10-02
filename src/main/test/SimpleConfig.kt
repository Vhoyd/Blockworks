package dev.vhoyd.blockworks.simple

import dev.vhoyd.blockworks.block.BlockDefinition
import dev.vhoyd.blockworks.core.Config
import dev.vhoyd.blockworks.core.ConfigProperty
import org.bukkit.plugin.Plugin

class SimpleConfig  : Config {

    constructor(
        plugin : Plugin,
        ignoreBreakingPower : Boolean = false,
        ignoreMiningFortune : Boolean = false,
        dynamicFortuneScaling : Boolean = false,
        roundingMethod: SimpleConfigProperty.RoundingMethod = SimpleConfigProperty.RoundingMethod.FLOOR,
        miningRateScale : Double = 1.0,
        blockDefinitionList : List<BlockDefinition>
    ) : super(plugin, blockDefinitionList,

        mapOf<ConfigProperty<*>, Any>(
            SimpleConfigProperty.IGNORE_BREAKING_POWER to ignoreBreakingPower,
            SimpleConfigProperty.IGNORE_MINING_FORTUNE to ignoreMiningFortune,
            SimpleConfigProperty.DYNAMIC_FORTUNE_SCALING to dynamicFortuneScaling,
            SimpleConfigProperty.MINING_RATE_SCALE to miningRateScale,
            SimpleConfigProperty.ROUNDING_METHOD to roundingMethod
        ))


}