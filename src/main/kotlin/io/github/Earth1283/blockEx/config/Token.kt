package io.github.Earth1283.blockEx.config

enum class TokenType {
    DIRECTIVE, IDENTIFIER, STRING, NUMBER, LBRACE, RBRACE, LPAREN, RPAREN, ARROW, RANGE, COMMA, EOF
}

data class Token(val type: TokenType, val value: String, val line: Int, val column: Int)
