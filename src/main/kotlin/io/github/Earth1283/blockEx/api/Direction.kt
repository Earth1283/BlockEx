package io.github.Earth1283.blockEx.api

enum class Direction(val vector: BlockVector3) {
    UP(BlockVector3(0, 1, 0)),
    DOWN(BlockVector3(0, -1, 0)),
    NORTH(BlockVector3(0, 0, -1)),
    SOUTH(BlockVector3(0, 0, 1)),
    EAST(BlockVector3(1, 0, 0)),
    WEST(BlockVector3(-1, 0, 0))
}
