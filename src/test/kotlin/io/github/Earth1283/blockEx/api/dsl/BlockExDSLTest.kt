package io.github.Earth1283.blockEx.api.dsl

import io.github.Earth1283.blockEx.engine.ast.StartNode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BlockExDSLTest {
    @Test
    fun testStartNodeDSL() {
        val pattern = blockEx {
            start("minecraft:diamond_block")
        }
        
        val ast = pattern.astRoot
        assertTrue(ast is StartNode)
        assertEquals("minecraft:diamond_block", (ast as StartNode).expectedMaterial)
    }
}
