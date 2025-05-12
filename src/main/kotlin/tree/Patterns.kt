package dev.tollernamen.tree

import dev.tollernamen.analytics.SourceLocation

sealed class Pattern(override val location: SourceLocation) : Tree

// 0 1 "Hello" 'c'
class LiteralPattern(
    location: SourceLocation,
    val value: String,
) : Pattern(location) {
    override val kind: TreeKind
        get() = TODO("Not yet implemented")
}

// str s myVar n
class IdentifierPattern(
    location: SourceLocation,
    val symbol: String,
) : Pattern(location) {
    override val kind: TreeKind
        get() = TODO("Not yet implemented")
}

class UnnamedIdentifierPattern(
    location: SourceLocation
) : Pattern(location) {
    override val kind: TreeKind
        get() = TODO("Not yet implemented")
}

enum class ArrayPatternOp {
    QUESTION,
    WILDCARD,
    PLUS,
}

// [a, _+] [a, _*] [a] [_?, a == 0, n*]
class ArrayPatternOperation(
    location: SourceLocation,
    val child: Pattern,
    val op: ArrayPatternOp
) : Pattern(location) {
    override val kind: TreeKind
        get() = TODO("Not yet implemented")
}

class ArrayPattern(
    location: SourceLocation,
    val patterns: List<Pattern>
) : Pattern(location) {
    override val kind: TreeKind
        get() = TODO("Not yet implemented")
}

class ObjectPattern(
    location: SourceLocation,
    val patterns: List<Pattern>
) : Pattern(location) {
    override val kind: TreeKind
        get() = TODO("Not yet implemented")
}

class PatternBinding(
    location: SourceLocation,
    val target: Pattern,
    val value: Pattern,
) : Pattern(location) {
    override val kind: TreeKind
        get() = TODO("Not yet implemented")
}

class TuplePattern(
    location: SourceLocation,
    val left: Pattern,
    val right: Pattern,
) : Pattern(location) {
    override val kind: TreeKind
        get() = TODO("Not yet implemented")
}