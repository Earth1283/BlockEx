package io.github.Earth1283.blockEx

import org.bukkit.plugin.java.JavaPlugin

class BlockEx : JavaPlugin() {
    override fun onEnable() {
        logger.info("BlockEx Engine Initialized.")
        // Event listeners and registry will be loaded here in future tasks
    }

    override fun onDisable() {
        logger.info("BlockEx Engine Disabled.")
    }
}
