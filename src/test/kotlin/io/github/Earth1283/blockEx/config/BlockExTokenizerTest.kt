package io.github.Earth1283.blockEx.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class BlockExTokenizerTest {
    @Test
    fun `should throw error on excessive identifier length`() {
        val longIdentifier = "a".repeat(2000)
        val tokenizer = BlockExTokenizer(longIdentifier)
        assertThrows(ParserException::class.java) {
            tokenizer.tokenize()
        }
    }

    @Test
    fun `should throw error on excessive string length`() {
        val longString = "\"" + "a".repeat(5000) + "\""
        val tokenizer = BlockExTokenizer(longString)
        assertThrows(ParserException::class.java) {
            tokenizer.tokenize()
        }
    }

    @Test
    fun `should throw error on unterminated string`() {
        val unterminated = "\"hello"
        val tokenizer = BlockExTokenizer(unterminated)
        assertThrows(ParserException::class.java) {
            tokenizer.tokenize()
        }
    }
    
    @Test
    fun testTokenizeDirectives() {
        val input = "@useCompiled @trigger"
        val tokens = BlockExTokenizer(input).tokenize()
        assertEquals(TokenType.DIRECTIVE, tokens[0].type)
        assertEquals("useCompiled", tokens[0].value)
        assertEquals(TokenType.DIRECTIVE, tokens[1].type)
        assertEquals("trigger", tokens[1].value)
        assertEquals(TokenType.EOF, tokens[2].type)
    }

    @Test
    fun testTokenizeFullExample() {
        val input = """
            @trigger(block:break)
            pattern "my_pattern" {
                1..5 -> { some_id }
            }
        """.trimIndent()
        val tokens = BlockExTokenizer(input).tokenize()
        
        var i = 0
        assertEquals(TokenType.DIRECTIVE, tokens[i].type); assertEquals("trigger", tokens[i++].value)
        assertEquals(TokenType.LPAREN, tokens[i++].type)
        assertEquals(TokenType.IDENTIFIER, tokens[i].type); assertEquals("block:break", tokens[i++].value)
        assertEquals(TokenType.RPAREN, tokens[i++].type)
        
        assertEquals(TokenType.IDENTIFIER, tokens[i].type); assertEquals("pattern", tokens[i++].value)
        assertEquals(TokenType.STRING, tokens[i].type); assertEquals("my_pattern", tokens[i++].value)
        assertEquals(TokenType.LBRACE, tokens[i++].type)
        
        assertEquals(TokenType.NUMBER, tokens[i].type); assertEquals("1", tokens[i++].value)
        assertEquals(TokenType.RANGE, tokens[i++].type)
        assertEquals(TokenType.NUMBER, tokens[i].type); assertEquals("5", tokens[i++].value)
        assertEquals(TokenType.ARROW, tokens[i++].type)
        assertEquals(TokenType.LBRACE, tokens[i++].type)
        assertEquals(TokenType.IDENTIFIER, tokens[i].type); assertEquals("some_id", tokens[i++].value)
        assertEquals(TokenType.RBRACE, tokens[i++].type)
        
        assertEquals(TokenType.RBRACE, tokens[i++].type)
        assertEquals(TokenType.EOF, tokens[i++].type)
    }
}
