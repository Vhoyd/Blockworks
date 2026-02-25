package dev.vhoyd.blockworks.test

import dev.vhoyd.blockworks.mining.Attribute
import org.bukkit.persistence.PersistentDataType

object SimpleMiningAttribute {
    val MINING_SPEED = Attribute("miningSpeed", PersistentDataType.FLOAT)
    val MINING_FORTUNE = Attribute("miningFortune", PersistentDataType.FLOAT)
    val BREAKING_POWER = Attribute("breakingPower", PersistentDataType.FLOAT)
    val BLOCK_STRENGTH = Attribute("blockStrength", PersistentDataType.INTEGER)
    val BLOCK_DAMAGE = Attribute("blockDamage", PersistentDataType.INTEGER)

}