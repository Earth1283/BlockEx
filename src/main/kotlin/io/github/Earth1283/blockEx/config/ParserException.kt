package io.github.Earth1283.blockEx.config

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

class ParserException(
    val messageText: String,
    val line: Int,
    val column: Int,
    val suggestion: String? = null
) : RuntimeException("$messageText at line $line, column $column") {

    fun toComponent(): Component {
        val base = Component.text()
            .append(Component.text("BlockEx Parser Error: ", NamedTextColor.RED))
            .append(Component.text(messageText, NamedTextColor.WHITE))
            .append(Component.text(" (Line $line, Col $column)", NamedTextColor.GRAY))
        
        return if (suggestion != null) {
            base.append(Component.newline())
                .append(Component.text("Suggestion: ", NamedTextColor.GREEN))
                .append(Component.text(suggestion, NamedTextColor.WHITE))
                .build()
        } else {
            base.build()
        }
    }
}
