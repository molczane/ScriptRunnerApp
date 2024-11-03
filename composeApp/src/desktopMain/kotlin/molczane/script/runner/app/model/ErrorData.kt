package molczane.script.runner.app.model

data class ErrorData(
    val message: String,
    val lineNumber: Int,
    val columnNumber: Int,
    val isClickable: Boolean = true
)
