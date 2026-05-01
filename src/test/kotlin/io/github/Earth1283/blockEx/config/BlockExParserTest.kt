package io.github.Earth1283.blockEx.config

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class BlockExParserTest {
    @Test
    fun testParseSimplePattern() {
        val input = "start(\"minecraft:stone\")->up(1, \"minecraft:grass\")"
        val pattern = BlockExParser.parse(input)
        assertNotNull(pattern)
    }

    @Test
    fun testParseWithDirective() {
        val input = """
            @useCompiled
            start("minecraft:stone")->up(1, "minecraft:grass")
        """.trimIndent()
        val pattern = BlockExParser.parse(input)
        assertNotNull(pattern)
    }

    @Test
    fun testParseWithRange() {
        val input = "start(\"minecraft:stone\")->up(1..3, \"minecraft:grass\")"
        val pattern = BlockExParser.parse(input)
        assertNotNull(pattern)
    }
}
