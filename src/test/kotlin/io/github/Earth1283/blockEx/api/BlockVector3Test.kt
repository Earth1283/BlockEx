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

    @Test
    fun testScalarMultiplication() {
        val a = BlockVector3(1, 2, 3)

        // Positive scalar
        assertEquals(BlockVector3(2, 4, 6), a * 2)

        // Zero scalar
        assertEquals(BlockVector3(0, 0, 0), a * 0)

        // Negative scalar
        assertEquals(BlockVector3(-1, -2, -3), a * -1)
    }

    @Test
    fun testVectorAdditionEdgeCases() {
        val a = BlockVector3(1, -1, 0)
        val b = BlockVector3(-1, 1, 0)

        // Negative components
        assertEquals(BlockVector3(0, 0, 0), a + b)

        // Zero components
        val zero = BlockVector3(0, 0, 0)
        assertEquals(a, a + zero)
    }
}
