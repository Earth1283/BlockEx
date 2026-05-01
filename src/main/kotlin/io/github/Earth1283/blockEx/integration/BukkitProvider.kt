package io.github.Earth1283.blockEx.integration

import io.github.Earth1283.blockEx.api.BlockProvider
import io.github.Earth1283.blockEx.api.BlockVector3
import org.bukkit.World

class BukkitProvider(private val world: World) : BlockProvider {
    override fun getMaterialAt(position: BlockVector3): String {
        return world.getBlockAt(position.x, position.y, position.z).type.key.toString()
    }
}
