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
import molczane.script.runner.app.model.ScriptState
import java.io.File
import java.io.InputStreamReader

class ScriptViewModel : ViewModel() {
    val errorList = mutableStateListOf<ErrorData>()
    var scriptState = mutableStateOf(ScriptState())
    var outputState = mutableStateOf("Output will appear here...")
    var isRunning = mutableStateOf(false)
    var exitCode = mutableStateOf<Int?>(null)
    val processList = mutableListOf<Process>() // Store references to running processes
    var fileToDestroy: File? = null

    // Lista słów kluczowych do wyróżnienia
    val keywords = setOf(
        "abstract", "annotation", "as", "break", "by", "catch", "class", "companion", "const",
        "constructor", "continue", "crossinline", "data", "delegate", "do", "else", "enum",
        "expect", "external", "false", "final", "finally", "for", "fun", "get", "if", "import",
        "in", "infix", "init", "inline", "inner", "interface", "internal", "is", "it", "lateinit",
        "noinline", "null", "object", "open", "operator", "out", "override", "package", "private",
        "protected", "public", "reified", "return", "sealed", "set", "super", "suspend", "tailrec",
        "this", "throw", "true", "try", "typealias", "typeof", "val", "var", "vararg", "when",
        "where", "while"
    )

    private val keywordRegex = Regex("\\b(${keywords.joinToString("|")})\\b")
    private val errorPattern = Regex("""(\w+\.kts):(\d+):(\d+):\s+error:\s+(.+)""")

    override fun onCleared() {
        super.onCleared()
        // Cancel any active coroutines or resources here
        viewModelScope.cancel()
    }

    fun updateScript(text: String) {
        scriptState.value = scriptState.value.copy(scriptText = text)
    }

    fun highlightSyntax(): List<Pair<String, Boolean>> {
        val words = scriptState.value.scriptText.split(Regex("(?=\\s)|(?<=\\s)"))
        return words.map { word ->
            if (keywordRegex.matches(word.trim())) {
                word to true // True oznacza, że słowo jest słowem kluczowym
            } else {
                word to false // False dla zwykłych słów
            }
        }
    }

    fun runScript() {
        val scriptContent = scriptState.value.scriptText
        isRunning.value = true
        outputState.value = ""
        errorList.clear() // List to store parsed errors

        viewModelScope.launch(Dispatchers.IO) {

            //val tempFile = File.createTempFile("tempScript", ".kts")
            val tempFile = File("foo.kts")
            fileToDestroy = tempFile
            tempFile.createNewFile()
            tempFile.writeText(scriptContent)
            val processCommand = ProcessBuilder("kotlinc", "-script", tempFile.absolutePath)

            try {
                val process = processCommand.start()
                processList.add(process)
                val outputReader = InputStreamReader(process.inputStream).buffered()
                val errorReader = InputStreamReader(process.errorStream).buffered()

                // Run both output and error reading within a coroutine scope
                coroutineScope {
                    val jobOutput = launch {
                        outputReader.lineSequence().forEach { line ->
                            // Update the state directly without switching context for smoother output handling
                            withContext(Dispatchers.Main) {
                                outputState.value += "$line\n"
                            }
                        }
                    }

                    val jobError = launch {
                        errorReader.lineSequence().forEach { line ->
                            withContext(Dispatchers.Main) {
                                outputState.value += "$line\n"

                                // Parse error output and store line/column
                                val matchResult = errorPattern.find(line)
                                if (matchResult != null) {
                                    val (file, lineNumber, columnNumber, _) = matchResult.destructured
                                    errorList.add(
                                        ErrorData(
                                            message = String.format("%s:%s:%s", file, lineNumber, columnNumber),
                                            lineNumber = lineNumber.toInt(),
                                            columnNumber = columnNumber.toInt()
                                        )
                                    )
                                }
                            }
                        }
                    }

                    // Wait for the jobs to finish
                    jobOutput.join()
                    jobError.join()
                }

                // Wait for the process to complete and the coroutines to finish reading
                val code = process.waitFor()

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
            } finally {
                tempFile.delete()
            }
        }
    }

    fun stopScript() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Iterate over all running processes and terminate them
                processList.forEach { process ->
                    if (process.isAlive) {
                        process.children().forEach { child ->
                            if(child.isAlive) {
                                child.destroyForcibly()
                            }
                        }
                        process.destroy() // Attempt graceful termination

                        // Wait for up to 5 seconds for the process to terminate
                        if (!process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
                            process.destroyForcibly() // Force termination if not stopped in 5 seconds
                            println("Process forcibly terminated.")
                        } else {
                            println("Process terminated gracefully.")
                        }
                    }
                }

                // Clear the process list after stopping the processes
                processList.clear()

                // Update the state to indicate that the process is no longer running
                withContext(Dispatchers.Main) {
                    isRunning.value = false
                    outputState.value += "\nScript execution stopped."
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    println("Error stopping the process: ${e.message}")
                    outputState.value += "\nError stopping the script: ${e.message}"
                }
            }
        }
    }

}