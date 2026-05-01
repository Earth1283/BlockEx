package io.github.Earth1283.blockEx.integration

import io.github.Earth1283.blockEx.api.dsl.Pattern
import org.bukkit.event.Listener

class TriggerRegistry : Listener {
    private val patterns = mutableMapOf<String, MutableList<Pattern>>()

    fun register(event: String, pattern: Pattern) {
        patterns.computeIfAbsent(event) { mutableListOf() }.add(pattern)
    }
    
    // For now, this just stores them. In a real scenario, we'd have @EventHandler methods.
}
