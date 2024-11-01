package molczane.script.runner.app.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import molczane.script.runner.app.model.ScriptState
import java.io.File
import java.io.InputStreamReader

class ScriptViewModel : ViewModel() {
    var scriptState = mutableStateOf(ScriptState())
    var outputState = mutableStateOf("Output will appear here...")
    var isRunning = mutableStateOf(false)
    var exitCode = mutableStateOf<Int?>(null)

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

        viewModelScope.launch(Dispatchers.IO) {  // }
//        CoroutineScope(Dispatchers.IO).launch {
            val tempFile = File.createTempFile("tempScript", ".kts")
            tempFile.writeText(scriptContent)

            try {
                val process = ProcessBuilder("kotlinc", "-script", tempFile.absolutePath).start()
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
}