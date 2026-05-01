package io.github.Earth1283.blockEx

import io.github.Earth1283.blockEx.config.BlockExParser
import io.github.Earth1283.blockEx.config.ParserException
import io.github.Earth1283.blockEx.integration.TriggerRegistry
import org.bukkit.plugin.java.JavaPlugin

class BlockEx : JavaPlugin() {
    private val triggerRegistry = TriggerRegistry()

    override fun onEnable() {
        logger.info("BlockEx Engine Initialized.")
        
        loadPatterns()
        
        server.pluginManager.registerEvents(triggerRegistry, this)
    }

    private fun loadPatterns() {
        val configDir = dataFolder.resolve("patterns")
        if (!configDir.exists()) {
            configDir.mkdirs()
            // Optional: copy example pattern from resources
        }

        configDir.listFiles { _, name -> name.endsWith(".blockex") }?.forEach { file ->
            try {
                val pattern = BlockExParser.parse(file)
                // In a future task, we would extract @trigger directive info here
                // For now, we just log success
                logger.info("Loaded pattern: ${file.name}")
            } catch (e: ParserException) {
                // Use Adventure to log rich error
                slF4JLogger.error("Failed to parse ${file.name}: ${e.message}")
            } catch (e: Exception) {
                logger.severe("Unexpected error loading ${file.name}: ${e.message}")
            }
        }
    }

    override fun onDisable() {
        logger.info("BlockEx Engine Disabled.")
    }
}
