package molczane.script.runner.app.utils

class KotlinSyntaxHighlighter : SyntaxHighlighter {
    override fun highlight(text: String): List<Pair<String, Boolean>> {
        val words = text.split(Regex("(?=\\s)|(?<=\\s)"))
        return words.map { word ->
            if (keywordRegex.matches(word.trim())) {
                word to true // True oznacza, że słowo jest słowem kluczowym
            } else {
                word to false // False dla zwykłych słów
            }
        }
    }

    override val keywords: Set<String>
        get() = setOf(
            "abstract", "annotation", "as", "break", "by", "catch", "class", "companion", "const",
            "constructor", "continue", "crossinline", "data", "delegate", "do", "else", "enum",
            "expect", "external", "false", "final", "finally", "for", "fun", "get", "if", "import",
            "in", "infix", "init", "inline", "inner", "interface", "internal", "is", "it", "lateinit",
            "noinline", "null", "object", "open", "operator", "out", "override", "package", "private",
            "protected", "public", "reified", "return", "sealed", "set", "super", "suspend", "tailrec",
            "this", "throw", "true", "try", "typealias", "typeof", "val", "var", "vararg", "when",
            "where", "while"
        )

    override val keywordRegex: Regex
        get() = Regex("\\b(${keywords.joinToString("|")})\\b")

    override val errorPattern: Regex
        get() = Regex("""(\w+\.kts):(\d+):(\d+):\s+error:\s+(.+)""")
}