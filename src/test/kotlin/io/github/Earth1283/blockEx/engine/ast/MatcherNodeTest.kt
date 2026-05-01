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
}
