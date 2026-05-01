package io.github.Earth1283.blockEx.config

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer

class ParserExceptionTest {

    @Test
    fun `toComponent should include message line and column`() {
        val exception = ParserException("Unexpected token", 1, 5)
        val component = exception.toComponent()
        val plainText = PlainTextComponentSerializer.plainText().serialize(component)
        
        assertTrue(plainText.contains("BlockEx Parser Error: Unexpected token"))
        assertTrue(plainText.contains("(Line 1, Col 5)"))
    }

    @Test
    fun `toComponent should include suggestion if present`() {
        val exception = ParserException("Missing bracket", 2, 10, "Add a '}'")
        val component = exception.toComponent()
        val plainText = PlainTextComponentSerializer.plainText().serialize(component)
        
        assertTrue(plainText.contains("Suggestion: Add a '}'"))
    }
}
