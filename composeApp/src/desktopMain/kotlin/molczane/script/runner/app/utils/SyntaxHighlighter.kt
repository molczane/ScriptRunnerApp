package molczane.script.runner.app.utils

interface SyntaxHighlighter {
    fun highlight(text: String): List<Pair<String, Boolean>>
    val keywords: Set<String>
    val keywordRegex : Regex
    val errorPattern : Regex
}