package io.github.Earth1283.blockEx.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BlockVector3Test {
    @Test
    fun testVectorAddition() {
        val a = BlockVector3(1, 0, 0)
        val b = Direction.UP.vector
        val c = a + b
        assertEquals(BlockVector3(1, 1, 0), c)
    }

    @Test
    fun testVectorSubtraction() {
        val a = BlockVector3(5, 5, 5)
        val b = BlockVector3(1, 2, 3)
        val c = a - b
        assertEquals(BlockVector3(4, 3, 2), c)
    }
}
