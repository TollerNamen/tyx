package dev.tollernamen.analytics.parsing

import dev.tollernamen.analytics.*
import dev.tollernamen.tree.*

class TypeParser(lexer: Lexer) : Parser<Type>(lexer) {
    override val parserName = "Type"
    override val nudMap = mapOf(
        TkKind.IDENTIFIER to (parseSymbol to BindingPower.PRIMARY),
        TkKind.BRACKET_OPEN to (parseArray to BindingPower.DEFAULT),
        TkKind.PAREN_OPEN to (parseGroup to BindingPower.DEFAULT),
        TkKind.PAREN_CLOSE to (parseNothing to BindingPower.DEFAULT),
        TkKind.LT to (parseGeneric to BindingPower.DEFAULT)
    )
    override val ledMap = mapOf(
        TkKind.COLON to (parseFunction to BindingPower.LABEL)
    )
}

private val parseSymbol: NUD_Handler<Type> = { p -> SymbolType(p.currentTk().location, p.nextTk().value) }

private val parseArray: NUD_Handler<Type> = { p ->
    val start = p.nextTk().location.start
    val child = p.next()
    p.expectCurrent(TkKind.BRACKET_CLOSE)
    ArrayType(SourceLocation(start, child.location.end), child)
}

private val parseGroup: NUD_Handler<Type> = { p ->
    p.nextTk()
    val child = p.next()
    p.expectCurrent(TkKind.PAREN_CLOSE)
    p.nextTk()
    child
}

private val parseNothing: NUD_Handler<Type> = { p -> NoType(p.currentTk().location) }

private val parseFunction: LED_Handler<Type> = { p, bp, left ->
    p.nextTk()
    val ret = p.next(bp)
    FunctionType(SourceLocation(left.location.start, ret.location.end), left, ret)
}

private val parseGeneric: NUD_Handler<Type> = { p ->
    val start = p.nextTk().location.start
    val symbols = mutableListOf(p.nextTk().value)
    while (p.currentTk().kind == TkKind.COMMA) {
        p.nextTk()
        p.expectCurrent(TkKind.IDENTIFIER)
        symbols.add(p.nextTk().value)
    }
    p.expectCurrent(TkKind.GT)
    val end = p.nextTk().location.end
    GenericTypes(SourceLocation(start, end), symbols)
}