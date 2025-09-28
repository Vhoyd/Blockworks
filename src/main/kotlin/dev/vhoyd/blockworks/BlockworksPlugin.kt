package dev.vhoyd.blockworks

import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.plugin.java.JavaPlugin

class BlockworksPlugin : JavaPlugin() {
    override fun onEnable() {
//        val text = Component.text("Blockworks has loaded! Please report " +
//                "issues to the mod developer Vhoyd, either on Discord or GitHub.")
//        text.color(TextColor.color(0, 255, 255))
        logger.info("Blockworks has loaded! Please report " +
                "issues to the mod developer Vhoyd, either on Discord or GitHub.")
    }
}