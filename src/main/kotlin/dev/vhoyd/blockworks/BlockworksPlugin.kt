package dev.vhoyd.blockworks

import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.scheduler.BukkitRunnable

class BlockworksPlugin : JavaPlugin() {
    override fun onEnable() {
        logger.info("Blockworks has loaded! Please report " +
                "issues to the mod developer Vhoyd, either on Discord or GitHub.")
//        SimpleMain.go(this)
    }
}