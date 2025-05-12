package dev.tollernamen.debug

import dev.tollernamen.analytics.Lexer
import dev.tollernamen.analytics.SourceLocation

sealed class Error(
    private val message: String, private val lexer: Lexer, private val location: SourceLocation
) {
    override fun toString(): String {
        infix operator fun String.times(int: Int) = repeat(int)

        val sourceLines = lexer.source.split(Regex("\\R"))
        val lineBoundary =
            (location.start.line - 3).coerceAtLeast(1) to
                    (location.end.line + 3).coerceAtMost(sourceLines.size - 1)
        val snippetLines = sourceLines.filterIndexed { index, _ ->
            lineBoundary.first <= (index + 1) && lineBoundary.second >= (index + 1)
        }.map { "  $it" }.toMutableList()

        val lineLength = lineBoundary.second.toString().length
        val coordinateSize = lineLength + 1 + (if (location.start.column < location.end.column) location.start.column
        else location.end.column)

        (lineBoundary.first..lineBoundary.second)
            .map {
                if (it >= location.start.line || it <= location.end.line) "$it" + " " * (lineLength - "$it".length) + ":"
                else "$it"
            }
            .map { "$it${" " * (coordinateSize - it.length)}|" }
            .forEachIndexed { i, s -> snippetLines[i] = s + snippetLines[i] }

        return """
            ${this.javaClass.simpleName}
            
            ${snippetLines.joinToString("\n") { it }}
            
            Message: $message
        """.trimIndent()
    }
}

class SyntaxError(
    message: String,
    lexer: Lexer,
    location: SourceLocation
) : Error(message, lexer, location)

class SemanticError(
    message: String,
    lexer: Lexer,
    location: SourceLocation
) : Error(message, lexer, location)