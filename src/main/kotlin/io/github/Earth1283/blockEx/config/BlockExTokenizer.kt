package io.github.Earth1283.blockEx.config

class BlockExTokenizer(private val input: String) {
    private var pos = 0
    private var line = 1
    private var col = 1

    fun tokenize(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (pos < input.length) {
            val char = input[pos]
            when {
                char.isWhitespace() -> {
                    if (char == '\n') { line++; col = 1 } else { col++ }
                    pos++
                }
                char == '@' -> {
                    val startCol = col
                    pos++
                    col++
                    val value = readIdentifier()
                    tokens.add(Token(TokenType.DIRECTIVE, value, line, startCol))
                }
                char == '-' && pos + 1 < input.length && input[pos + 1] == '>' -> {
                    tokens.add(Token(TokenType.ARROW, "->", line, col))
                    pos += 2
                    col += 2
                }
                char == '(' -> { tokens.add(Token(TokenType.LPAREN, "(", line, col)); pos++; col++ }
                char == ')' -> { tokens.add(Token(TokenType.RPAREN, ")", line, col)); pos++; col++ }
                char == '{' -> { tokens.add(Token(TokenType.LBRACE, "{", line, col)); pos++; col++ }
                char == '}' -> { tokens.add(Token(TokenType.RBRACE, "}", line, col)); pos++; col++ }
                char == ',' -> { tokens.add(Token(TokenType.COMMA, ",", line, col)); pos++; col++ }
                char == '"' || char == '\'' -> tokens.add(readString(char))
                char.isDigit() -> tokens.add(readNumber())
                char.isLetter() || char == ':' || char == '_' -> tokens.add(readIdentifierToken())
                char == '.' && pos + 1 < input.length && input[pos + 1] == '.' -> {
                    tokens.add(Token(TokenType.RANGE, "..", line, col))
                    pos += 2
                    col += 2
                }
                else -> { pos++; col++ } // Ignore unknown for now
            }
        }
        tokens.add(Token(TokenType.EOF, "", line, col))
        return tokens
    }

    private fun readIdentifier(): String {
        val start = pos
        while (pos < input.length && (input[pos].isLetterOrDigit() || input[pos] == '_' || input[pos] == ':')) {
            pos++
            col++
            if (pos - start > 1024) throw ParserException("Identifier exceeds max length of 1024", line, col)
        }
        return input.substring(start, pos)
    }

    private fun readIdentifierToken(): Token {
        val startCol = col
        val value = readIdentifier()
        return Token(TokenType.IDENTIFIER, value, line, startCol)
    }

    private fun readNumber(): Token {
        val startCol = col
        val start = pos
        while (pos < input.length && input[pos].isDigit()) { 
            pos++; col++ 
            if (pos - start > 128) throw ParserException("Number exceeds max length of 128", line, col)
        }
        return Token(TokenType.NUMBER, input.substring(start, pos), line, startCol)
    }

    private fun readString(quote: Char): Token {
        val startCol = col
        pos++ // skip start quote
        col++
        val start = pos
        while (pos < input.length && input[pos] != quote) { 
            pos++; col++ 
            if (pos - start > 4096) throw ParserException("String literal exceeds max length of 4096", line, col)
        }
        if (pos >= input.length) throw ParserException("Unterminated string literal", line, col)
        val value = input.substring(start, pos)
        pos++ // skip end quote
        col++
        return Token(TokenType.STRING, value, line, startCol)
    }
}
