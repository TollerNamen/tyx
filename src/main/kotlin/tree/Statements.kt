package dev.tollernamen.tree

import dev.tollernamen.analytics.SourceLocation
import dev.tollernamen.analytics.TkKind

sealed class Statement(override val location: SourceLocation) : Tree

enum class Modifier {
    ACCESS_MY,
    ACCESS_NONE,
    NATIVE,
    FORCE,
}

fun asModifier(kind: TkKind): Modifier {
    return Modifier.valueOf(kind.name)
}

class Declaration(
    location: SourceLocation,
    val name: String,
    val modifier: List<Modifier>,
    val type: Type,
    val value: Expression?
) : Statement(location) {
    override val kind = TreeKind.DECLARATION
    override fun toString(): String {
        return "Declaration(name=$name)"
    }
}