package molczane.script.runner.app.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import molczane.script.runner.app.model.ErrorData
import molczane.script.runner.app.utils.syntax.ScriptingLanguage
import molczane.script.runner.app.model.ScriptState
import molczane.script.runner.app.service.KotlinScriptExecutor
import molczane.script.runner.app.service.ScriptExecutionManager
import molczane.script.runner.app.service.SwiftScriptExecutor
import molczane.script.runner.app.utils.syntax.KotlinSyntaxHighlighter
import molczane.script.runner.app.utils.syntax.SwiftSyntaxHighlighter
import java.io.File

class ScriptViewModel : ViewModel() {
    var errorList = mutableListOf<ErrorData>()
    var scriptState = mutableStateOf(ScriptState())
    var outputState = mutableStateOf("Output will appear here...")
    var isRunning = mutableStateOf(false)
    var exitCode = mutableStateOf<Int?>(null)
    var fileToDestroy: File? = null
    var selectedScriptingLanguage = mutableStateOf(ScriptingLanguage.Kotlin)

    val executorMap = mapOf(
        ScriptingLanguage.Kotlin to KotlinScriptExecutor(),
        ScriptingLanguage.Swift to SwiftScriptExecutor()
    )

    val syntaxHighlighterMap = mapOf(
        ScriptingLanguage.Kotlin to KotlinSyntaxHighlighter(),
        ScriptingLanguage.Swift to SwiftSyntaxHighlighter()
    )

    override fun onCleared() {
        super.onCleared()
        // Cancel any active coroutines or resources here
        viewModelScope.cancel()
    }

    fun updateScript(text: String) {
        scriptState.value = scriptState.value.copy(scriptText = text)
    }

    fun highlightSyntax() : List<Pair<String, Boolean>> {
        val highlighter = syntaxHighlighterMap[selectedScriptingLanguage.value]

        return highlighter?.highlight(scriptState.value.scriptText) ?: emptyList()
    }

    private val scriptExecutionManager = ScriptExecutionManager(
        executorMap = executorMap,
        errorCallback = { error ->
            // Tworzy nową listę z istniejącymi elementami i nowym błędem
            val updatedList = errorList.toList() + error
            // Przypisuje nową listę do `errorList`, aby wymusić rekompozycję
            errorList.clear()
            errorList = updatedList.toMutableStateList()

            println("${error}") },
        outputCallback = { line -> outputState.value += "$line\n" }
    )

    fun runScript() {
        val scriptContent = scriptState.value.scriptText
        outputState.value = ""
        errorList.clear()
        viewModelScope.launch {
            scriptExecutionManager.runScript(
                scriptContent = scriptContent,
                language = selectedScriptingLanguage.value,
                isRunningCallback = { isRunning.value = it },
                exitCodeCallback = { exitCode.value = it }
            )
        }
    }

    fun stopScript() {
        viewModelScope.launch {
            scriptExecutionManager.stopScript()
            isRunning.value = false
            exitCode.value = null
        }
    }

//    fun runScript() {
//        val scriptContent = scriptState.value.scriptText
//        isRunning.value = true
//        outputState.value = ""
//        errorList.clear() // List to store parsed errors
//
//        viewModelScope.launch(Dispatchers.IO) {
//            val executor = executorMap[selectedScriptingLanguage.value] ?: return@launch
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
//                                val highlighter = syntaxHighlighterMap[selectedScriptingLanguage.value]
//
//                                val matchResult = highlighter?.errorPattern?.find(line)
//
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
//
//    fun stopScript() {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                // Iterate over all running processes and terminate them
//                processList.forEach { process ->
//                    if (process.isAlive) {
//                        process.children().forEach { child ->
//                            if(child.isAlive) {
//                                child.destroyForcibly()
//                            }
//                        }
//                        process.destroy() // Attempt graceful termination
//
//                        // Wait for up to 5 seconds for the process to terminate
//                        if (!process.waitFor(5, java.util.concurrent.TimeUnit.SECONDS)) {
//                            process.destroyForcibly() // Force termination if not stopped in 5 seconds
//                            println("Process forcibly terminated.")
//                        } else {
//                            println("Process terminated gracefully.")
//                        }
//                    }
//                }
//
//                // Clear the process list after stopping the processes
//                processList.clear()
//
//                // Update the state to indicate that the process is no longer running
//                withContext(Dispatchers.Main) {
//                    isRunning.value = false
//                    exitCode.value = null
//                    outputState.value += "\nScript execution stopped."
//                }
//            } catch (e: Exception) {
//                withContext(Dispatchers.Main) {
//                    println("Error stopping the process: ${e.message}")
//                    outputState.value += "\nError stopping the script: ${e.message}"
//                }
//            }
//        }
//    }
}

