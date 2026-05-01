package io.github.Earth1283.blockEx.engine.compiled

import io.github.Earth1283.blockEx.api.BlockVector3
import io.github.Earth1283.blockEx.api.Direction
import io.github.Earth1283.blockEx.engine.ast.DirectionNode
import io.github.Earth1283.blockEx.engine.ast.StartNode
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
}
