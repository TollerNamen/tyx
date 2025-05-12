package dev.tollernamen.tree

import dev.tollernamen.analytics.SourceLocation

sealed class Expression(override val location: SourceLocation) : Tree

class NoExpression(
    location: SourceLocation
) : Expression(location) {
    override val kind: TreeKind
        get() = TODO("Not yet implemented")
}

class CallExpr(
    location: SourceLocation,
    val callee: Expression,
    val arg: Expression
) : Expression(location) {
    override val kind = TreeKind.CALL
}

class TernaryExpr(
    location: SourceLocation,
    val condition: Expression,
    val success: Expression,
    val failure: Expression
) : Expression(location) {
    override val kind = TreeKind.TERNARY
}

class PreUnaryExpr(
    location: SourceLocation,
    val op: String,
    val right: Expression
) : Expression(location) {
    override val kind = TreeKind.UNARY_PRE
}

class BinaryExpr(
    location: SourceLocation,
    val left: Expression,
    val right: Expression,
    val op: String
) : Expression(location) {
    override val kind = TreeKind.BINARY
}

sealed class LiteralExpr(
    location: SourceLocation,
    open val value: String
) : Expression(location)

class IdentifierLiteralExpr(
    location: SourceLocation,
    override val value: String
) : LiteralExpr(location, value) {
    override val kind = TreeKind.IDENTIFIER
}

class NumberLiteralExpr(
    location: SourceLocation,
    override val value: String
) : LiteralExpr(location, value) {
    override val kind = TreeKind.NUMBER
}

class CharLiteralExpr(
    location: SourceLocation,
    override val value: String
) : LiteralExpr(location, value) {
    override val kind = TreeKind.CHAR
}

class StringLiteralExpr(
    location: SourceLocation,
    override val value: String
) : LiteralExpr(location, value) {
    override val kind = TreeKind.STRING
}
