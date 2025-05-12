package dev.tollernamen.debug

import dev.tollernamen.tree.*

fun exprToString(e: Expression): String {
    return when (e) {
        is BinaryExpr -> "Binary(op=${e.op}, left=${e.left}, right=${e.right})"
        is CallExpr -> "Call(callee=${e.callee}, argument=${e.arg})"
        is LiteralExpr -> when (e) {
            is IdentifierLiteralExpr -> "Identifier(\"${e.value}\")"
            is CharLiteralExpr -> "Character('${e.value}')"
            is NumberLiteralExpr -> "Number(${e.value})"
            is StringLiteralExpr -> "String(\"${e.value}\")"
        };
        is PreUnaryExpr -> "PreUnary(op=${e.op}, right=${e.right})"
        is TernaryExpr -> "Ternary(condition=${e.condition}, success=${e.success}, failure=${e.failure})"
        is NoExpression -> ""
    }
}

fun stmtToString(s: Statement): String {
    return when (s) {
        is Declaration -> "Declaration(name=\"${s.name}\", modifiers=${
            s.modifier.joinToString()
        }, value=${s.value})"
    }
}

fun typeToString(t: Type): String {
    return when (t) {
        is SymbolType -> t.symbol
        is ArrayType -> "[${typeToString(t.type)}]"
        is FunctionType -> "(${typeToString(t.arg)}:${typeToString(t.ret)})"
        is NoType -> ""
        is ObjectType -> if (t.members.isEmpty()) "{}"
            else t.members.joinToString("; ", "{", "}") { typeToString(it) }
        is ObjectTypeMember -> "${t.name} ${typeToString(t.type)}"
        is GenericTypes -> t.symbols.joinToString(prefix = "<", postfix = ">") { it }
    }
}

fun Any.toPrettyDebugString(indentWidth: Int = 2) = buildString {
    fun StringBuilder.indent(level: Int) = append("".padStart(level * indentWidth))
    var ignoreSpace = false
    var indentLevel = 0
    this@toPrettyDebugString.toString().onEach {
        when (it) {
            '(', '[', '{' -> appendLine(it).indent(++indentLevel)
            ')', ']', '}' -> appendLine().indent(--indentLevel).append(it)
            ','           -> appendLine(it).indent(indentLevel).also { ignoreSpace = true }
            ' '           -> if (ignoreSpace) ignoreSpace = false else append(it)
            '='           -> append(" = ")
            else          -> append(it)
        }
    }
}
