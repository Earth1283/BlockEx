package io.github.Earth1283.blockEx.config

import io.github.Earth1283.blockEx.api.dsl.Pattern
import io.github.Earth1283.blockEx.api.dsl.PatternBuilder
import io.github.Earth1283.blockEx.api.dsl.ChainBuilder
import java.io.File

object BlockExParser {
    fun parse(file: File): Pattern = parse(file.readText())

    fun parse(input: String): Pattern {
        val tokens = BlockExTokenizer(input).tokenize()
        return ParserInternal(tokens).parse()
    }

    private class ParserInternal(val tokens: List<Token>) {
        private var current = 0
        private val builder = PatternBuilder()

        fun parse(): Pattern {
            while (!isAtEnd()) {
                val token = peek()
                if (token.type == TokenType.DIRECTIVE) {
                    handleDirective(consume().value)
                } else if (token.type == TokenType.IDENTIFIER && token.value == "start") {
                    handleStart()
                } else {
                    consume() // skip unknown top-level or throw error
                }
            }
            return builder.build()
        }

        private fun handleDirective(name: String) {
            when (name) {
                "useCompiled" -> builder.useCompiled = true
                "trigger" -> skipBlock() // Registry handled separately later
                "action" -> skipBlock()
                else -> throw ParserException("Unknown directive @$name", peek().line, peek().column)
            }
        }

        private fun handleStart() {
            consume() // start
            consume(TokenType.LPAREN, "Expected '(' after 'start'")
            val mat = consume(TokenType.STRING, "Expected material name").value
            consume(TokenType.RPAREN, "Expected ')' after material")
            
            val chain = builder.start(mat)
            parseChain(chain)
        }

        private fun parseChain(chain: ChainBuilder) {
            while (match(TokenType.ARROW)) {
                val func = consume(TokenType.IDENTIFIER, "Expected direction function").value
                consume(TokenType.LPAREN, "Expected '('")
                
                val countToken = consume()
                val range = if (match(TokenType.RANGE)) {
                    val last = consume(TokenType.NUMBER, "Expected end of range").value.toInt()
                    countToken.value.toInt()..last
                } else {
                    countToken.value.toInt()..countToken.value.toInt()
                }
                
                consume(TokenType.COMMA, "Expected ','")
                val mat = consume(TokenType.STRING, "Expected material").value
                consume(TokenType.RPAREN, "Expected ')'")
                
                // Invoke corresponding method on ChainBuilder
                when (func) {
                    "up" -> chain.up(range, mat)
                    "down" -> chain.down(range, mat)
                    "north" -> chain.north(range, mat)
                    "south" -> chain.south(range, mat)
                    "east" -> chain.east(range, mat)
                    "west" -> chain.west(range, mat)
                    else -> throw ParserException("Unknown direction function '$func'", peek().line, peek().column)
                }
            }
            
            if (peek().type == TokenType.IDENTIFIER && peek().value == "branch") {
                consume()
                consume(TokenType.LBRACE, "Expected '{'")
                chain.branch {
                   parseChain(this)
                }
                consume(TokenType.RBRACE, "Expected '}'")
            }
        }

        private fun skipBlock() {
            consume(TokenType.LBRACE, "Expected '{'")
            var depth = 1
            while (depth > 0 && !isAtEnd()) {
                val t = consume()
                if (t.type == TokenType.LBRACE) depth++
                if (t.type == TokenType.RBRACE) depth--
            }
        }

        private fun consume(type: TokenType, message: String): Token {
            if (check(type)) return consume()
            val token = peek()
            throw ParserException(message, token.line, token.column)
        }

        private fun consume(): Token = tokens[current++]
        private fun match(type: TokenType): Boolean = if (check(type)) { consume(); true } else false
        private fun check(type: TokenType): Boolean = !isAtEnd() && peek().type == type
        private fun peek(): Token = tokens[current]
        private fun isAtEnd(): Boolean = peek().type == TokenType.EOF
    }
}
