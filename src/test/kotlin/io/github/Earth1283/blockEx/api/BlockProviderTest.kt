package io.github.Earth1283.blockEx.api

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BlockProviderTest {

    @Test
    fun `test BlockProvider mock implementation`() {
        val mockProvider = object : BlockProvider {
            override fun getMaterialAt(position: BlockVector3): String {
                return if (position.y >= 64) "minecraft:air" else "minecraft:stone"
            }
        }

        assertEquals("minecraft:air", mockProvider.getMaterialAt(BlockVector3(0, 64, 0)))
        assertEquals("minecraft:stone", mockProvider.getMaterialAt(BlockVector3(0, 63, 0)))
    }
}
