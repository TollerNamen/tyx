package dev.tollernamen.analytics.parsing

import dev.tollernamen.analytics.*
import dev.tollernamen.tree.*

class ExpressionParser(lexer: Lexer, parseLedPatternMatching: Boolean = false) : Parser<Expression>(lexer) {
    override val parserName = "Expression"
    override val nudMap = mapOf(
        TkKind.IDENTIFIER to (parseLiteral to BindingPower.PRIMARY),
        TkKind.NUMBER to (parseLiteral to BindingPower.PRIMARY),
        TkKind.STRING to (parseLiteral to BindingPower.PRIMARY),
        TkKind.CHAR to (parseLiteral to BindingPower.PRIMARY),

        TkKind.PLUS to (parsePreUnary to BindingPower.UNARY),
        TkKind.MINUS to (parsePreUnary to BindingPower.UNARY),
    )
    override val ledMap = mapOf(
        TkKind.DOT to (parseDotCall to BindingPower.CALL),

        TkKind.QUESTION to (parseTernary to BindingPower.TERNARY),

        TkKind.PLUS to (parseBinary to BindingPower.ADDITIVE),
        TkKind.MINUS to (parseBinary to BindingPower.ADDITIVE),
        TkKind.STAR to (parseBinary to BindingPower.MULTIPLICATIVE),
        TkKind.SLASH to (parseBinary to BindingPower.MULTIPLICATIVE),
        TkKind.STAR_STAR to (parseBinary to BindingPower.POW),

        TkKind.COLON to (parseBinary to BindingPower.LABEL)
    )

    override fun next(bp: BindingPower): Expression {
        fun getNUD() = nudMap[currentTk().kind]?.first
        fun getLED(): LED_Handler<Expression>? {
            var maybeLED = ledMap[currentTk().kind]?.first
            if (maybeLED == null && getNUD() != null)
                maybeLED = parsePostCall
            return maybeLED
        }

        fun getBP() = when (getLED()) {
            parsePostCall -> BindingPower.CALL.ordinal
            null -> 0
            else -> ledMap[currentTk().kind]?.second?.ordinal ?: 0
        }

        val nud = getNUD()!!
        var left: Expression = nud(this);
        var currentBP = getBP()

        lateinit var led: LED_Handler<Expression>

        while (currentBP > bp.ordinal) {
            led = getLED()!!
            left = led(this, bp, left)
            currentBP = getBP()
        }
        return left
    }
}

// foo arg1 arg2
private val parsePostCall: LED_Handler<Expression> = { p, _, left ->
    val arg = p.next(BindingPower.CALL)
    CallExpr(
        SourceLocation(
            left.location.start, arg.location.end
        ), left, arg
    )
}

// arg1.foo
private val parseDotCall: LED_Handler<Expression> = { p, _, left ->
    p.nextTk()
    val callee = p.next(BindingPower.CALL)
    CallExpr(
        SourceLocation(
            left.location.start, callee.location.start
        ), callee, left
    )
}

private val parseTernary: LED_Handler<Expression> = { p, bp, left ->
    p.nextTk()
    val success = p.next()
    p.expectCurrent(TkKind.COLON)
    val failure = p.next(bp)
    TernaryExpr(
        SourceLocation(
            left.location.start, failure.location.end
        ), left, success, failure
    )
}
private val parseBinary: LED_Handler<Expression> = { p, bp, left ->
    val op = p.nextTk().value
    println(op)
    println(p.currentTk())
    val right = p.next(bp)
    BinaryExpr(
        SourceLocation(
            left.location.start, right.location.end
        ), left, right, op
    )
}
private val parsePreUnary: NUD_Handler<Expression> = { p ->
    val tk = p.nextTk()
    val right = p.next()
    PreUnaryExpr(
        SourceLocation(
            tk.location.start, right.location.start
        ), op = tk.value, right
    )
}
private val parseLiteral: NUD_Handler<Expression> = { p ->
    val tk = p.nextTk()
    when (tk.kind) {
        TkKind.IDENTIFIER -> IdentifierLiteralExpr(tk.location, tk.value)
        TkKind.NUMBER -> NumberLiteralExpr(tk.location, tk.value)
        TkKind.STRING -> StringLiteralExpr(tk.location, tk.value)
        TkKind.CHAR -> CharLiteralExpr(tk.location, tk.value)
        else -> error("Unexpected token '${p.currentTk().value}' in parse literal fun!")
    }
}
