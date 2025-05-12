package dev.tollernamen

import dev.tollernamen.analytics.Lexer
import dev.tollernamen.analytics.parsing.StatementParser
import dev.tollernamen.debug.stmtToString

fun main() {
    //Files.readString(Path("example.tyx"))
    val parser = StatementParser(Lexer("""
        head <T>([T]:T) = a: a[0];
        tail <T>([T]:T) = a: a[a.size - 1];
    """.trimIndent()))
    while (parser.hasNext()) {
        println(stmtToString(parser.next()))
    }
}