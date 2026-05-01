package io.github.Earth1283.blockEx.api

/** Provides block material strings (e.g., "minecraft:stone") at specific coordinates. */
interface BlockProvider {
    fun getMaterialAt(position: BlockVector3): String
}
