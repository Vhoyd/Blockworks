package dev.vhoyd.blockworks

import org.bukkit.plugin.java.JavaPlugin

/**
 * Required for servers to not throw a fit.
 */
class BlockworksPlugin : JavaPlugin() {
    override fun onEnable() {
        logger.info("Thank you for using Blockworks! Please report " +
                "issues to the mod developer Vhoyd, either on Discord or GitHub.")
    }
}