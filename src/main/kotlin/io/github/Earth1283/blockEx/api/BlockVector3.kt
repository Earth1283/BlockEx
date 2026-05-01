package io.github.Earth1283.blockEx.api

data class BlockVector3(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: BlockVector3) = BlockVector3(x + other.x, y + other.y, z + other.z)
    operator fun minus(other: BlockVector3) = BlockVector3(x - other.x, y - other.y, z - other.z)
    operator fun times(scalar: Int) = BlockVector3(x * scalar, y * scalar, z * scalar)
}
