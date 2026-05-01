package io.github.Earth1283.blockEx.integration

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player

object ActionDispatcher {
    fun dispatchCommand(command: String, player: Player, loc: Location) {
        val finalCmd = command
            .replace("%player%", player.name)
            .replace("%x%", loc.blockX.toString())
            .replace("%y%", loc.blockY.toString())
            .replace("%z%", loc.blockZ.toString())
            .replace("%world%", loc.world.name)
        
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCmd)
    }
}
