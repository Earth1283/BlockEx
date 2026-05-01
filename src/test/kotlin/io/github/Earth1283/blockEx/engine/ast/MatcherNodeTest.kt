package io.github.Earth1283.blockEx.engine.ast

import io.github.Earth1283.blockEx.api.BlockProvider
import io.github.Earth1283.blockEx.api.BlockVector3
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MatcherNodeTest {
    private val mockProvider = object : BlockProvider {
        private val world = mutableMapOf<BlockVector3, String>()
        fun setBlock(pos: BlockVector3, material: String) { world[pos] = material }
        override fun getMaterialAt(pos: BlockVector3): String = world[pos] ?: "AIR"
    }

    @Test
    fun `StartNode should match correct material`() {
        val pos = BlockVector3(0, 0, 0)
        mockProvider.setBlock(pos, "STONE")
        
        val node = StartNode("STONE")
        assertTrue(node.matches(mockProvider, pos))
        
        val mismatchNode = StartNode("DIRT")
        assertFalse(mismatchNode.matches(mockProvider, pos))
    }

    @Test
    fun `StartNode with wildcard should match any material`() {
        val pos = BlockVector3(0, 0, 0)
        mockProvider.setBlock(pos, "STONE")
        
        val node = StartNode("*")
        assertTrue(node.matches(mockProvider, pos))
    }

    @Test
    fun `DirectionNode should match sequence with backtracking`() {
        // STONE -> 1..2 GOLD -> IRON
        // World: (0,0,0) STONE, (1,0,0) GOLD, (2,0,0) GOLD, (3,0,0) IRON
        val p0 = BlockVector3(0, 0, 0)
        val p1 = BlockVector3(1, 0, 0)
        val p2 = BlockVector3(2, 0, 0)
        val p3 = BlockVector3(3, 0, 0)
        
        mockProvider.setBlock(p0, "STONE")
        mockProvider.setBlock(p1, "GOLD")
        mockProvider.setBlock(p2, "GOLD")
        mockProvider.setBlock(p3, "IRON")
        
        val ironNode = DirectionNode(io.github.Earth1283.blockEx.api.Direction.EAST, 1..1, "IRON")
        val goldNode = DirectionNode(io.github.Earth1283.blockEx.api.Direction.EAST, 1..2, "GOLD", ironNode)
        val stoneNode = StartNode("STONE", goldNode)
        
        assertTrue(stoneNode.matches(mockProvider, p0))
        
        // Test backtracking: if we match 2 GOLD, but then IRON is missing
        mockProvider.setBlock(p3, "DIRT")
        // But IRON might be at p2? No, p2 is GOLD.
        assertFalse(stoneNode.matches(mockProvider, p0))
        
        // Test backtracking: World: STONE, GOLD, IRON, DIRT
        // 1 GOLD matches, then IRON matches.
        mockProvider.setBlock(p2, "IRON")
        assertTrue(stoneNode.matches(mockProvider, p0))
    }
}
