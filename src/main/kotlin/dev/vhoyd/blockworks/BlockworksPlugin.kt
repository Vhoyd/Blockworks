package dev.vhoyd.blockworks

import org.bukkit.plugin.java.JavaPlugin

class BlockworksPlugin : JavaPlugin() {
    override fun onEnable() {
        logger.info("Blockworks has loaded! Please report " +
                "issues to the mod developer Vhoyd, either on Discord or GitHub.")
    }
}