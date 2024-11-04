package molczane.script.runner.app.utils.syntax

class SwiftSyntaxHighlighter : SyntaxHighlighter {
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
            "associatedtype", "class", "deinit", "enum", "extension", "func", "import", "init", "inout",
            "let", "operator", "precedencegroup", "protocol", "struct", "subscript", "typealias", "var",
            "break", "case", "continue", "default", "defer", "do", "else", "fallthrough", "for", "guard",
            "if", "in", "repeat", "return", "switch", "where", "while", "as", "Any", "catch", "false",
            "is", "nil", "rethrows", "super", "self", "Self", "throw", "throws", "true", "try", "__COLUMN__",
            "__FILE__", "__FUNCTION__", "__LINE__"
        )
    override val keywordRegex: Regex
        get() = Regex("\\b(${keywords.joinToString("|")})\\b")
    override val errorPattern: Regex
        get() = Regex("""(\w+\.swift):(\d+):(\d+):\s+error:\s+(.+)""")
}