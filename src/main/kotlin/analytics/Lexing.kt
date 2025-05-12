package dev.tollernamen.analytics

import dev.tollernamen.debug.SyntaxError

data class Token(
    val kind: TkKind, val value: String, val location: SourceLocation
) {
    override fun toString(): String {
        return "Token(kind=$kind, value='$value')"
    }
}

data class SourcePointer(
    val pos: Int,
    val line: Int,
    val column: Int
)

data class SourceLocation(
    val start: SourcePointer,
    val end: SourcePointer
)

enum class TkKind {
    WHITE_SPACE, INVALID_STR_CHAR,

    IDENTIFIER, NUMBER, STRING, CHAR,

    // keywords
    LET, IN, MY, NATIVE, FORCE, THIS, TYPE,

    // symbols
    DOT, COMMA,
    PLUS, MINUS, STAR_STAR, STAR, SLASH,

    GT, GTEQ, LT, LTEQ,

    QUESTION, COLON,

    EQ,

    BRACKET_OPEN, BRACKET_CLOSE, PAREN_OPEN, PAREN_CLOSE, WITH_SLASH, PIPE,

    SEMI,

    EOF, ERROR;

    companion object {
        val skipableKinds = listOf(WHITE_SPACE, INVALID_STR_CHAR)
    }
}

val keywords = listOf(
    "let", "in", "my", "native", "force", "type", "this"
)

interface TkProcessor {
    val handler: TokenHandler
    fun startsWith(source: String, offset: Int): Boolean
    fun getValue(source: String, offset: Int): String
}

class RegexTkProcessor(
    private val regex: Regex, override val handler: TokenHandler
) : TkProcessor {
    constructor(regex: String, handler: TokenHandler) : this(Regex(regex), handler)

    override fun startsWith(source: String, offset: Int): Boolean {
        return regex.matchesAt(source, offset)
    }

    override fun getValue(source: String, offset: Int): String {
        return regex.find(source, offset)!!.value
    }
}

class BasicTkProcessor(
    private val value: String, override val handler: TokenHandler
) : TkProcessor {
    override fun startsWith(source: String, offset: Int): Boolean {
        return source.startsWith(value, offset)
    }

    override fun getValue(source: String, offset: Int): String {
        return value; }
}

class Lexer(val source: String) : Iterator<Token> {
    private var line: Int = 1
    private var column: Int = 0
    private var position: Int = 0
    private var tkProcessor: List<TkProcessor>
    var current: Token

    override fun hasNext(): Boolean {
        return position < source.length
    }

    override fun next(): Token {
        val tmp = current
        current = nextTk()
        return tmp
    }

    private fun nextTk(): Token {
        if (!hasNext()) return Token(TkKind.EOF, "EOF", simpleLocation(0))

        val proc = tkProcessor.find { it.startsWith(source, position) }
        if (proc == null) {
            val err = source[position].toString()
            println("Could not tokenize '$err'!\nSkipping...")
            position++
            column++
            return Token(TkKind.ERROR, err, simpleLocation(1))
        }
        val value = proc.getValue(source, position)
        position += value.length
        column += value.length
        val tk = proc.handler(value)
        return if (TkKind.skipableKinds.contains(tk.kind)) nextTk() else tk
    }

    private fun simpleLocation(length: Int): SourceLocation {
        return SourceLocation(
            SourcePointer(position, line, column), SourcePointer(position + length, line, column + length)
        )
    }

    private fun defaultHandler(tkKind: TkKind) = { value: String -> Token(tkKind, value, simpleLocation(value.length)) }

    private fun identifierHandler(value: String): Token {
        val location = simpleLocation(value.length)
        if (keywords.contains(value)) Token(TkKind.valueOf(value.uppercase()), value, location)
        return Token(TkKind.IDENTIFIER, value, location)
    }
    private fun whitespaceHandler(value: String): Token {
        val matches = Regex("\\R").findAll(value).toList()
        val startLine = line
        val startColumn = column
        matches.forEach { _ -> line++ }
        column = value.length - if (matches.isNotEmpty()) matches.last().range.last else 0
        return Token(
            TkKind.WHITE_SPACE, value, SourceLocation(
                SourcePointer(position, startLine, startColumn), SourcePointer(position + value.length, line, column)
            )
        )
    }
    private fun invalidStrCharHandler(value: String): Token {
        val location = simpleLocation(value.length)
        error(SyntaxError("Invalid String or Char literal found.", this, location))
    }

    init {
        tkProcessor = listOf(
            RegexTkProcessor("(\\s|\\R)+", ::whitespaceHandler),

            BasicTkProcessor("w/", defaultHandler(TkKind.WITH_SLASH)),
            RegexTkProcessor("[a-zA-Z_]\\w*", ::identifierHandler),
            RegexTkProcessor("[0-9]+(\\.[0-9]+)?", defaultHandler(TkKind.NUMBER)),

            RegexTkProcessor("\"(\\s|.)*\"", defaultHandler(TkKind.STRING)),
            RegexTkProcessor("'(\\s|.)*'", defaultHandler(TkKind.CHAR)),
            RegexTkProcessor("[\"'](\\s|.)*\\R", ::invalidStrCharHandler),

            BasicTkProcessor("**", defaultHandler(TkKind.STAR_STAR)),

            BasicTkProcessor("<=", defaultHandler(TkKind.LTEQ)),
            BasicTkProcessor(">=", defaultHandler(TkKind.GTEQ)),
            BasicTkProcessor("<", defaultHandler(TkKind.LT)),
            BasicTkProcessor(">", defaultHandler(TkKind.GT)),

            BasicTkProcessor(".", defaultHandler(TkKind.DOT)),
            BasicTkProcessor(",", defaultHandler(TkKind.COMMA)),

            BasicTkProcessor("*", defaultHandler(TkKind.STAR)),
            BasicTkProcessor("/", defaultHandler(TkKind.SLASH)),
            BasicTkProcessor("+", defaultHandler(TkKind.PLUS)),
            BasicTkProcessor("-", defaultHandler(TkKind.MINUS)),

            BasicTkProcessor("?", defaultHandler(TkKind.QUESTION)),
            BasicTkProcessor(":", defaultHandler(TkKind.COLON)),

            BasicTkProcessor("=", defaultHandler(TkKind.EQ)),

            BasicTkProcessor("[", defaultHandler(TkKind.BRACKET_OPEN)),
            BasicTkProcessor("]", defaultHandler(TkKind.BRACKET_CLOSE)),
            BasicTkProcessor("(", defaultHandler(TkKind.PAREN_OPEN)),
            BasicTkProcessor(")", defaultHandler(TkKind.PAREN_CLOSE)),
            BasicTkProcessor(".", defaultHandler(TkKind.DOT)),
            BasicTkProcessor("|", defaultHandler(TkKind.PIPE)),

            BasicTkProcessor(";", defaultHandler(TkKind.SEMI)),
        )
        current = nextTk()
    }
}

typealias TokenHandler = (String) -> Token