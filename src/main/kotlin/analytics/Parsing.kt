package dev.tollernamen.analytics

abstract class Parser<T>(val lexer: Lexer) {
    fun nextTk() = lexer.next()
    fun currentTk() = lexer.current

    fun expectCurrent(kind: TkKind) {
        if (currentTk().kind != kind)
            error("Unexpected token '${currentTk().value}' during $parserName parsing, expected $kind instead")
    }

    fun expectCurrent(kinds: List<TkKind>) {
        if (!kinds.contains(currentTk().kind))
            error(
                "Unexpected token '${currentTk().value}' during $parserName parsing, expected either ${
                    kinds.joinToString(" or ") { kind -> kind.toString() }
                } instead")
    }

    fun hasNext() = lexer.hasNext()

    open fun next(bp: BindingPower = BindingPower.DEFAULT): T {
        fun getBP(): Int {
            return ledMap[currentTk().kind]?.second?.ordinal ?: 0
        }

        val nud = nudMap[currentTk().kind]!!.first
        var left: T = nud(this);
        var currentBP = getBP()

        lateinit var tk: Token
        lateinit var led: LED_Handler<T>

        while (currentBP > bp.ordinal) {
            tk = currentTk()
            led = ledMap[tk.kind]!!.first
            left = led(this, bp, left)
            currentBP = getBP()
        }
        return left
    }

    abstract val parserName: String
    abstract val nudMap: Map<TkKind, Pair<NUD_Handler<T>, BindingPower>>
    abstract val ledMap: Map<TkKind, Pair<LED_Handler<T>, BindingPower>>
}

typealias NUD_Handler<T> = (Parser<T>) -> T
typealias LED_Handler<T> = (Parser<T>, BindingPower, T) -> T

enum class BindingPower {
    DEFAULT,
    COMMA,
    ASSIGNMENT,
    LAMBDA,
    TERNARY,
    LOGICAL, // boolean
    RELATIONAL,
    ADDITIVE,
    MULTIPLICATIVE,
    UNARY,
    POW,
    CALL,
    LABEL, // label
    PRIMARY,
}