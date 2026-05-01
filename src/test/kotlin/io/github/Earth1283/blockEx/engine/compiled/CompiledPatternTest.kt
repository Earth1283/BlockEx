package io.github.Earth1283.blockEx.engine.compiled

import io.github.Earth1283.blockEx.api.BlockVector3
import io.github.Earth1283.blockEx.api.Direction
import io.github.Earth1283.blockEx.engine.ast.DirectionNode
import io.github.Earth1283.blockEx.engine.ast.StartNode
import io.github.Earth1283.blockEx.engine.ast.BranchNode
import io.github.Earth1283.blockEx.engine.ast.MatcherNode
import io.github.Earth1283.blockEx.api.BlockProvider
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CompiledPatternTest {
    class MockProvider : BlockProvider {
        override fun getMaterialAt(position: BlockVector3): String {
            if (position == BlockVector3(0, 0, 0)) return "minecraft:diamond_block"
            if (position == BlockVector3(0, 1, 0)) return "minecraft:glass"
            return "minecraft:air"
        }
    }

    @Test
    fun testCompileSuccess() {
        val root = StartNode("minecraft:diamond_block", DirectionNode(Direction.UP, 1..1, "minecraft:glass", null))
        val compiled = CompiledPattern.compile(root)
        assertTrue(compiled.matches(MockProvider(), BlockVector3(0, 0, 0)))
    }
    
    @Test
    fun testCompileFailureVariableRange() {
        val root = StartNode("stone", DirectionNode(Direction.UP, 1..2, "glass", null))
        assertThrows<IllegalArgumentException> {
            CompiledPattern.compile(root)
        }
    }

    @Test
    fun testCompileBranchNode() {
        val branch1 = DirectionNode(Direction.UP, 1..1, "minecraft:glass", null)
        val branch2 = DirectionNode(Direction.NORTH, 1..1, "minecraft:stone", null)
        val root = StartNode("minecraft:diamond_block", BranchNode(listOf(branch1, branch2), null))
        
        val compiled = CompiledPattern.compile(root)
        val provider = object : BlockProvider {
            override fun getMaterialAt(position: BlockVector3): String {
                return when (position) {
                    BlockVector3(0, 0, 0) -> "minecraft:diamond_block"
                    BlockVector3(0, 1, 0) -> "minecraft:glass"
                    BlockVector3(0, 0, -1) -> "minecraft:stone"
                    else -> "minecraft:air"
                }
            }
        }
        assertTrue(compiled.matches(provider, BlockVector3(0, 0, 0)))
    }

    @Test
    fun testCompileWildcard() {
        val root = StartNode("*", DirectionNode(Direction.UP, 1..1, "minecraft:glass", null))
        val compiled = CompiledPattern.compile(root)
        val provider = object : BlockProvider {
            override fun getMaterialAt(position: BlockVector3): String = if (position == BlockVector3(0, 1, 0)) "minecraft:glass" else "any"
        }
        assertTrue(compiled.matches(provider, BlockVector3(0, 0, 0)))
    }

    @Test
    fun testCompileConflictingMaterials() {
        val branch1 = DirectionNode(Direction.UP, 1..1, "minecraft:glass", null)
        val branch2 = DirectionNode(Direction.UP, 1..1, "minecraft:stone", null)
        val root = StartNode("minecraft:diamond_block", BranchNode(listOf(branch1, branch2), null))
        
        assertThrows<IllegalStateException> {
            CompiledPattern.compile(root)
        }
    }
    
    @Test
    fun testCompileLongChain() {
        var node: MatcherNode = DirectionNode(Direction.UP, 1..1, "stone", null)
        for (i in 0 until 10) {
            node = DirectionNode(Direction.UP, 1..1, "stone", node)
        }
        val root = StartNode("diamond_block", node)
        val compiled = CompiledPattern.compile(root)
        // Check if it has 12 entries (StartNode + 11 DirectionNodes)
        assertTrue(compiled.offsets.size == 12)
    }
}
