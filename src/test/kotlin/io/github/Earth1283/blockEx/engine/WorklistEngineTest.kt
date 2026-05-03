package io.github.Earth1283.blockEx.engine

import io.github.Earth1283.blockEx.api.BlockProvider
import io.github.Earth1283.blockEx.api.BlockVector3
import io.github.Earth1283.blockEx.api.dsl.blockEx
import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis
import org.junit.jupiter.api.Assertions.assertTrue

class WorklistEngineTest {

    class StoneProvider : BlockProvider {
        override fun getMaterialAt(pos: BlockVector3): String = "stone"
    }

    @Test
    fun `reproduce catastrophic backtracking`() {
        // A pattern that should match, but will backtrack a lot if it fails at the end
        // up(0..10) -> up(0..10) -> ... -> up(0..10) -> north(1, "air")
        // The north(1, "air") will always fail in StoneProvider, 
        // causing all previous nodes to try all distances in their ranges.
        
        val depth = 50 // Much deeper
        val pattern = blockEx {
            var chain = start("stone")
            repeat(depth) {
                chain = chain.up(0..10, "stone")
            }
            chain.north(1, "air") // This will fail
        }

        val provider = StoneProvider()
        val origin = BlockVector3(0, 0, 0)

        val time = measureTimeMillis {
            pattern.matches(provider, origin)
        }
        
        println("Backtracking with depth $depth took $time ms")
        assertTrue(time < 1000, "Backtracking took too long: $time ms")
    }
}
