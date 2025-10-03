import dev.vhoyd.blockworks.mining.MiningAttribute
import org.bukkit.persistence.PersistentDataType

class SimpleMiningAttribute {
    companion object {
        val MINING_SPEED = MiningAttribute("miningSpeed", PersistentDataType.FLOAT)
        val MINING_FORTUNE = MiningAttribute("miningFortune", PersistentDataType.FLOAT)
        val BREAKING_POWER = MiningAttribute("breakingPower", PersistentDataType.FLOAT)
        val BLOCK_STRENGTH = MiningAttribute("blockStrength", PersistentDataType.INTEGER)
        val BLOCK_DAMAGE = MiningAttribute("blockDamage", PersistentDataType.INTEGER)
    }

}