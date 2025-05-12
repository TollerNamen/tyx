package dev.tollernamen.tree

import dev.tollernamen.analytics.SourceLocation

enum class TreeKind {
    DECLARATION,

    EXPR,
    CALL,

    TERNARY,
    BINARY,
    UNARY_PRE,

    IDENTIFIER,
    STRING,
    NUMBER,
    CHAR,

    TYPE_TUPLE,
    TYPE_GENERIC,
    TYPE_OBJECT,
    TYPE_OBJECT_MEMBER,
    TYPE_FUNCTION,
    TYPE_ARRAY,
    TYPE_SYMBOL,
    TYPE_NO,
}

sealed interface Tree {
    val kind: TreeKind
    val location: SourceLocation
}