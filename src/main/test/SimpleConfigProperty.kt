import dev.vhoyd.blockworks.core.ConfigProperty

class SimpleConfigProperty {
    companion object {
        val IGNORE_BREAKING_POWER = ConfigProperty("ignoreBreakingPower", Boolean::class.java)
        val IGNORE_MINING_FORTUNE = ConfigProperty("ignoreMiningFortune", Boolean::class.java)
        val DYNAMIC_FORTUNE_SCALING = ConfigProperty("dynamicFortuneScaling", Boolean::class.java)
        val MINING_RATE_SCALE = ConfigProperty("miningRateScale", Float::class.java)
        val ROUNDING_METHOD = ConfigProperty("roundingMethod", RoundingMethod::class.java)
    }

    enum class RoundingMethod {
        CEILING,
        FLOOR,
        ROUND
    }
}