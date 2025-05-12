package dev.tollernamen.analytics.parsing

import dev.tollernamen.analytics.*
import dev.tollernamen.tree.*

class StatementParser(lexer: Lexer) : Parser<Statement>(lexer) {
    override val parserName = "Statement"
    override val nudMap = mapOf(
        Pair(TkKind.IDENTIFIER, ::parseDeclaration to BindingPower.DEFAULT),

        Pair(TkKind.MY, ::parseModifier to BindingPower.DEFAULT),
        Pair(TkKind.NATIVE, ::parseModifier to BindingPower.DEFAULT),
        Pair(TkKind.FORCE, ::parseModifier to BindingPower.DEFAULT),
    )
    override val ledMap: Map<TkKind, Pair<LED_Handler<Statement>, BindingPower>> = mapOf()
}

private fun parseModifier(p: Parser<Statement>): Statement {
    val modifiers: MutableList<Modifier> = mutableListOf()
    while (p.currentTk().kind != TkKind.IDENTIFIER) {
        modifiers.add(asModifier(p.nextTk().kind))
    }
    return parseDeclarationWithModifiers(p, modifiers)
}

private fun parseDeclaration(p: Parser<Statement>): Statement {
    return parseDeclarationWithModifiers(p)
}

private fun parseDeclarationWithModifiers(p: Parser<Statement>, modifiers: List<Modifier> = listOf()): Statement {
    val start = p.currentTk().location.start
    val name = p.nextTk().value
    var type: Type = NoType(p.currentTk().location)
    var value: Expression = NoExpression(p.currentTk().location)
    if (p.currentTk().kind != TkKind.EQ) {
        type = TypeParser(p.lexer).next()
    }
    if (p.currentTk().kind == TkKind.EQ) {
        p.nextTk()
        value = ExpressionParser(p.lexer).next()
    }

    p.expectCurrent(TkKind.SEMI)
    val end = p.nextTk().location.end

    return Declaration(
        SourceLocation(
            start, end
        ), name, modifiers, type, value
    )
}
