package io.github.Earth1283.blockEx.api.dsl

import io.github.Earth1283.blockEx.api.Direction
import io.github.Earth1283.blockEx.engine.ast.DirectionNode
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

    @Test
    fun testDirectionalDSL() {
        val pattern = blockEx {
            start("minecraft:diamond_block")
                .up(3, "minecraft:glass")
                .down(1, "minecraft:stone")
                .north(2, "minecraft:oak_log")
                .south(1..2, "minecraft:leaves")
        }
        
        val ast = pattern.astRoot as StartNode
        val upNode = ast.next as DirectionNode
        assertEquals(io.github.Earth1283.blockEx.api.Direction.UP, upNode.direction)
        assertEquals(3..3, upNode.range)
        
        val downNode = upNode.next as DirectionNode
        assertEquals(io.github.Earth1283.blockEx.api.Direction.DOWN, downNode.direction)
        
        val northNode = downNode.next as DirectionNode
        assertEquals(io.github.Earth1283.blockEx.api.Direction.NORTH, northNode.direction)
        
        val southNode = northNode.next as DirectionNode
        assertEquals(io.github.Earth1283.blockEx.api.Direction.SOUTH, southNode.direction)
        assertEquals(1..2, southNode.range)
    }

    @Test
    fun testBranchingDSL() {
        val pattern = blockEx {
            start("minecraft:stone")
                .branch {
                    up(1, "minecraft:glass")
                }
                .branch {
                    down(1, "minecraft:glass")
                }
        }
        
        val ast = pattern.astRoot as StartNode
        assertTrue(ast.next is io.github.Earth1283.blockEx.engine.ast.BranchNode)
        val branch1 = ast.next as io.github.Earth1283.blockEx.engine.ast.BranchNode
        assertTrue(branch1.branches[0] is io.github.Earth1283.blockEx.engine.ast.DirectionNode)
        
        assertTrue(branch1.next is io.github.Earth1283.blockEx.engine.ast.BranchNode)
        val branch2 = branch1.next as io.github.Earth1283.blockEx.engine.ast.BranchNode
        assertTrue(branch2.branches[0] is io.github.Earth1283.blockEx.engine.ast.DirectionNode)
    }
}
