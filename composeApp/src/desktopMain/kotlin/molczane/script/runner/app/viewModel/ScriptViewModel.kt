package molczane.script.runner.app.viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import molczane.script.runner.app.model.ErrorData
import molczane.script.runner.app.utils.ScriptingLanguage
import molczane.script.runner.app.model.ScriptState
import molczane.script.runner.app.service.KotlinScriptExecutor
import molczane.script.runner.app.service.ScriptExecutor
import molczane.script.runner.app.service.ScriptRunner
import molczane.script.runner.app.service.SwiftScriptExecutor
import java.io.File
import java.util.concurrent.TimeUnit

class ScriptViewModel : ViewModel() {
    val errorList = mutableStateListOf<ErrorData>()
    var scriptState = mutableStateOf(ScriptState())
    var outputState = mutableStateOf("Output will appear here...")
    var isRunning = mutableStateOf(false)
    var exitCode = mutableStateOf<Int?>(null)
    private val processList = mutableListOf<Process>() // Store references to running processes
    var fileToDestroy: File? = null
    var selectedScriptingLanguage = mutableStateOf(ScriptingLanguage.Kotlin)

    private val executorMap: Map<ScriptingLanguage, ScriptExecutor> = mapOf(
        ScriptingLanguage.Kotlin to KotlinScriptExecutor(),
        ScriptingLanguage.Swift to SwiftScriptExecutor()
    )

    private val scriptRunner = ScriptRunner(
        executorMap = executorMap
    )

    // Lista słów kluczowych do wyróżnienia
    private val kotlinKeywords = setOf(
        "abstract", "annotation", "as", "break", "by", "catch", "class", "companion", "const",
        "constructor", "continue", "crossinline", "data", "delegate", "do", "else", "enum",
        "expect", "external", "false", "final", "finally", "for", "fun", "get", "if", "import",
        "in", "infix", "init", "inline", "inner", "interface", "internal", "is", "it", "lateinit",
        "noinline", "null", "object", "open", "operator", "out", "override", "package", "private",
        "protected", "public", "reified", "return", "sealed", "set", "super", "suspend", "tailrec",
        "this", "throw", "true", "try", "typealias", "typeof", "val", "var", "vararg", "when",
        "where", "while"
    )

    private val swiftKeywords = setOf(
        "associatedtype", "class", "deinit", "enum", "extension", "func", "import", "init", "inout",
        "let", "operator", "precedencegroup", "protocol", "struct", "subscript", "typealias", "var",
        "break", "case", "continue", "default", "defer", "do", "else", "fallthrough", "for", "guard",
        "if", "in", "repeat", "return", "switch", "where", "while", "as", "Any", "catch", "false",
        "is", "nil", "rethrows", "super", "self", "Self", "throw", "throws", "true", "try", "__COLUMN__",
        "__FILE__", "__FUNCTION__", "__LINE__"
    )


    private val kotlinKeywordRegex = Regex("\\b(${kotlinKeywords.joinToString("|")})\\b")
    private val swiftKeywordRegex = Regex("\\b(${swiftKeywords.joinToString("|")})\\b")

    private val kotlinErrorPattern = Regex("""(\w+\.kts):(\d+):(\d+):\s+error:\s+(.+)""")
    private val swiftErrorPattern = Regex("""(\w+\.swift):(\d+):(\d+):\s+error:\s+(.+)""")


    override fun onCleared() {
        super.onCleared()
        // Cancel any active coroutines or resources here
        viewModelScope.cancel()
    }

    fun updateScript(text: String) {
        scriptState.value = scriptState.value.copy(scriptText = text)
    }

    fun highlightKotlinSyntax(): List<Pair<String, Boolean>> {
        val words = scriptState.value.scriptText.split(Regex("(?=\\s)|(?<=\\s)"))
        return words.map { word ->
            if (kotlinKeywordRegex.matches(word.trim())) {
                word to true // True oznacza, że słowo jest słowem kluczowym
            } else {
                word to false // False dla zwykłych słów
            }
        }
    }

    fun highlightSwiftSyntax(): List<Pair<String, Boolean>> {
        val words = scriptState.value.scriptText.split(Regex("(?=\\s)|(?<=\\s)"))
        return words.map { word ->
            if (swiftKeywordRegex.matches(word.trim())) {
                word to true // True indicates the word is a keyword
            } else {
                word to false // False for regular words
            }
        }
    }


    fun runScript() {
        isRunning.value = true
        outputState.value = ""
        errorList.clear()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val code = scriptRunner.runScript(
                    scriptContent = scriptState.value.scriptText,
                    language = selectedScriptingLanguage.value,
                    outputCallback = { line ->
                        viewModelScope.launch(Dispatchers.IO) {
                            outputState.value += "$line\n"
                        }
                    },
                    errorCallback = { line ->
                        viewModelScope.launch(Dispatchers.IO) {
                            parseAndAddError(line)
                        }
                    }
                )

                withContext(Dispatchers.Main) {
                    exitCode.value = code
                    isRunning.value = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    outputState.value = "Error running script: ${e.message}"
                    exitCode.value = -1
                    isRunning.value = false
                }
            }
        }
    }

    private fun parseAndAddError(line: String) {
        val matchResult = when (selectedScriptingLanguage.value) {
            ScriptingLanguage.Kotlin -> kotlinErrorPattern.find(line)
            ScriptingLanguage.Swift -> swiftErrorPattern.find(line)
        }

        matchResult?.let {
            val (_, lineNumber, columnNumber, _) = it.destructured
            errorList.add(
                ErrorData(
                    message = line,
                    lineNumber = lineNumber.toInt(),
                    columnNumber = columnNumber.toInt(),
                    isClickable = true
                )
            )
        } ?: run {
            errorList.add(
                ErrorData(
                    message = line,
                    lineNumber = 0,
                    columnNumber = 0,
                    isClickable = false
                )
            )
        }
    }

//    fun runScript() {
//        val scriptContent = scriptState.value.scriptText
//        isRunning.value = true
//        outputState.value = ""
//        errorList.clear() // List to store parsed errors
//
//        viewModelScope.launch(Dispatchers.IO) {
//
//            val executor = executorMap[selectedScriptingLanguage.value] ?: run {
//                println("No executor found for the selected scripting language.")
//                return@launch // or return Unit if necessary
//            }
//
//            val tempFile = executor.prepareScriptFile(scriptState.value.scriptText)
//            val processCommand = executor.getProcessBuilder(tempFile)
//
//            fileToDestroy = tempFile
//            tempFile.createNewFile()
//            tempFile.writeText(scriptContent)
//
//            try {
//                val process = processCommand.start()
//                processList.add(process)
//                val outputReader = InputStreamReader(process.inputStream).buffered()
//                val errorReader = InputStreamReader(process.errorStream).buffered()
//
//                // Run both output and error reading within a coroutine scope
//                coroutineScope {
//                    val jobOutput = launch {
//                        outputReader.lineSequence().forEach { line ->
//                            // Update the state directly without switching context for smoother output handling
//                            withContext(Dispatchers.Main) {
//                                outputState.value += "$line\n"
//                                println("Reading output line: $line") // Debug line in jobOutput coroutine
//                            }
//                        }
//                    }
//
//                    val jobError = launch {
//                        errorReader.lineSequence().forEach { line ->
//                            withContext(Dispatchers.Main) {
//                                outputState.value += "$line\n"
//
//                                var matchResult : MatchResult? = null
//                                when (selectedScriptingLanguage.value) {
//                                    ScriptingLanguage.Kotlin -> matchResult = kotlinErrorPattern.find(line)
//                                    ScriptingLanguage.Swift -> matchResult = swiftErrorPattern.find(line)
//                                }
//                                if (matchResult != null) {
//                                    val (file, lineNumber, columnNumber, _) = matchResult.destructured
//                                    when(selectedScriptingLanguage.value) {
//                                        ScriptingLanguage.Kotlin -> errorList.add(
//                                            ErrorData(
//                                                message = String.format(
//                                                    "%s:%s:%s",
//                                                    file,
//                                                    lineNumber,
//                                                    columnNumber,
//                                                ),
//                                                lineNumber = lineNumber.toInt(),
//                                                columnNumber = columnNumber.toInt(),
//                                                isClickable = true
//                                            )
//                                        )
//                                        ScriptingLanguage.Swift -> errorList.add(
//                                            ErrorData(
//                                                message = line,
//                                                lineNumber = lineNumber.toInt(),
//                                                columnNumber = columnNumber.toInt(),
//                                                isClickable = true
//                                            )
//                                        )
//                                    }
//                                    println("Reading error line: $line") // Debug line in jobError coroutine
//                                }
//                                else {
//                                    errorList.add(
//                                        ErrorData(
//                                            message = line,
//                                            lineNumber = 0,
//                                            columnNumber = 0,
//                                            isClickable = false
//                                        )
//                                    )
//                                }
//                                //println("Error list size: ${errorList.size}")
//                            }
//                        }
//                    }
//
//                    // Wait for the jobs to finish
//                    jobOutput.join()
//                    jobError.join()
//                }
//
//                // Wait for the process to complete and the coroutines to finish reading
//                val code = process.waitFor()
//
//                withContext(Dispatchers.Main) {
//                    exitCode.value = code
//                    isRunning.value = false
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    outputState.value = "Error running script: ${e.message}"
//                    exitCode.value = -1
//                    isRunning.value = false
//                }
//            } finally {
//                tempFile.delete()
//            }
//        }
//    }

    fun stopScript() {
        viewModelScope.launch {
            scriptRunner.stopScript()
            isRunning.value = false
            exitCode.value = null
            outputState.value += "\nScript execution stopped."
        }
    }
}