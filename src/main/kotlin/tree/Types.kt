package dev.tollernamen.tree

import dev.tollernamen.analytics.SourceLocation

sealed class Type(override val location: SourceLocation) : Tree

class ObjectType(
    location: SourceLocation,
    val members: List<ObjectTypeMember>
) : Type(location) {
    override val kind = TreeKind.TYPE_OBJECT
}

class ObjectTypeMember(
    location: SourceLocation,
    val name: String,
    val type: Type
) : Type(location) {
    override val kind = TreeKind.TYPE_OBJECT_MEMBER
}

class FunctionType(
    location: SourceLocation,
    val arg: Type,
    val ret: Type,
) : Type(location) {
    override val kind = TreeKind.TYPE_FUNCTION
}

class ArrayType(
    location: SourceLocation,
    val type: Type
) : Type(location) {
    override val kind = TreeKind.TYPE_ARRAY
}

class SymbolType(
    location: SourceLocation,
    val symbol: String
) : Type(location) {
    override val kind = TreeKind.TYPE_SYMBOL
}

class NoType(
    location: SourceLocation
) : Type(location) {
    override val kind = TreeKind.TYPE_NO
}

class GenericTypes(
    location: SourceLocation,
    val symbols: List<String>
) : Type(location) {
    override val kind = TreeKind.TYPE_GENERIC
}