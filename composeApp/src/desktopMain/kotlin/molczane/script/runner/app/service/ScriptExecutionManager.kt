package molczane.script.runner.app.service

import kotlinx.coroutines.*
import molczane.script.runner.app.model.ErrorData
import molczane.script.runner.app.utils.syntax.ScriptingLanguage
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class ScriptExecutionManager(
    private val executorMap: Map<ScriptingLanguage, ScriptExecutor>,
    private val errorCallback: (ErrorData) -> Unit,
    private val outputCallback: (String) -> Unit
) {
    private val processList = mutableListOf<Process>()

    suspend fun runScript(
        scriptContent: String,
        language: ScriptingLanguage,
        isRunningCallback: (Boolean) -> Unit,
        exitCodeCallback: (Int?) -> Unit
    ) = withContext(Dispatchers.IO) {
        val executor = executorMap[language] ?: throw IllegalArgumentException("No executor found for $language")
        val tempFile = executor.prepareScriptFile(scriptContent)
        val process = executor.getProcessBuilder(tempFile).start()
        processList.add(process)
        isRunningCallback(true)

        try {
            val outputJob = launch { readProcessOutput(process) }
            val errorJob = launch { readProcessErrors(process, language) }

            // Wait for all output to be processed
            outputJob.join()
            errorJob.join()

            // Wait for process to complete
            val exitCode = process.waitFor()
            exitCodeCallback(exitCode)
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                outputCallback("Error running script: ${e.message}")
                exitCodeCallback(-1)
            }
        } finally {
            tempFile.delete()
            isRunningCallback(false)
            processList.remove(process)
        }
    }

    private suspend fun readProcessOutput(process: Process) = withContext(Dispatchers.IO) {
        val outputReader = InputStreamReader(process.inputStream).buffered()
        outputReader.lineSequence().forEach { line ->
            outputCallback(line) // Call directly in the I/O context to avoid blocking Main thread
        }
    }

    private suspend fun readProcessErrors(process: Process, language: ScriptingLanguage) = withContext(Dispatchers.IO) {
        val errorReader = InputStreamReader(process.errorStream).buffered()
        errorReader.lineSequence().forEach { line ->
            // Add the error line to the output callback
            withContext(Dispatchers.Main) {
                outputCallback(line)
            }

            val kotlinErrorPattern = Regex("""(\w+\.kts):(\d+):(\d+):\s+error:\s+(.+)""")
            val swiftErrorPattern = Regex("""(\w+\.swift):(\d+):(\d+):\s+error:\s+(.+)""")

            // Parse and create an ErrorData entry if applicable
            val matchResult = when(language) {
                ScriptingLanguage.Kotlin -> kotlinErrorPattern.find(line)
                ScriptingLanguage.Swift -> swiftErrorPattern.find(line)
            }
            if (matchResult != null) {
                val (file, lineNumber, columnNumber, _) = matchResult.destructured
                when(language) {
                    ScriptingLanguage.Kotlin -> errorCallback(
                        ErrorData(
                            message = String.format(
                                "%s:%s:%s",
                                file,
                                lineNumber,
                                columnNumber,
                            ),
                            lineNumber = lineNumber.toInt(),
                            columnNumber = columnNumber.toInt(),
                            isClickable = true
                        )
                    )
                    ScriptingLanguage.Swift -> errorCallback(
                        ErrorData(
                            message = line,
                            lineNumber = lineNumber.toInt(),
                            columnNumber = columnNumber.toInt(),
                            isClickable = true
                        )
                    )
                }
                println("Reading error line: $line") // Debug line in jobError coroutine
            }
            else {
                errorCallback(
                    ErrorData(
                        message = line,
                        lineNumber = 0,
                        columnNumber = 0,
                        isClickable = false
                    )
                )
            }
        }
    }

    suspend fun stopScript() {
        withContext(Dispatchers.IO) {
            try {
                processList.forEach { process ->
                    if (process.isAlive) {
                        process.children().forEach { it.destroyForcibly() }
                        process.destroy()
                        if (!process.waitFor(5, TimeUnit.SECONDS)) {
                            process.destroyForcibly()
                            outputCallback("Process forcibly terminated.")
                        } else {
                            outputCallback("\nScript execution stopped.")
                        }
                    }
                }
                processList.clear()
            } catch (e: Exception) {
                outputCallback("Error stopping the process: ${e.message}")
            }
        }
    }
}
