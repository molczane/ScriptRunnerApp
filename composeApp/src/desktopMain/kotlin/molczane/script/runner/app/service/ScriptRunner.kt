package molczane.script.runner.app.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import molczane.script.runner.app.utils.ScriptingLanguage
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit

class ScriptRunner(private val executorMap: Map<ScriptingLanguage, ScriptExecutor>) {

    private val processList = mutableListOf<Process>()

    suspend fun runScript(scriptContent: String, language: ScriptingLanguage, outputCallback: (String) -> Unit, errorCallback: (String) -> Unit): Int {
        val executor = executorMap[language] ?: throw IllegalStateException("Executor not found")
        val tempFile = executor.prepareScriptFile(scriptContent)
        val process = withContext(Dispatchers.IO) {
            executor.getProcessBuilder(tempFile).start()
        }

        processList.add(process)

        return try {
            coroutineScope {
                withContext(Dispatchers.IO) {
                    readProcessOutput(process, outputCallback)
                    readProcessErrors(process, errorCallback)
                }
            }
            process.waitFor()
        } finally {
            tempFile.delete()
        }
    }

    suspend fun stopScript() {
        withContext(Dispatchers.IO) {
            try {
                // Iterate over all running processes and terminate them
                processList.forEach { process ->
                    if (process.isAlive) {
                        process.children().forEach { child ->
                            if (child.isAlive) {
                                child.destroyForcibly()
                            }
                        }
                        process.destroy() // Attempt graceful termination

                        // Wait for up to 5 seconds for the process to terminate
                        if (!process.waitFor(5, TimeUnit.SECONDS)) {
                            process.destroyForcibly() // Force termination if not stopped in 5 seconds
                            println("Process forcibly terminated.")
                        } else {
                            println("Process terminated gracefully.")
                        }
                    }
                }

                // Clear the process list after stopping the processes
                processList.clear()

                println("All processes have been stopped.")
            } catch (e: Exception) {
                println("Error stopping the process: ${e.message}")
            }
        }
    }


    private suspend fun readProcessOutput(process: Process, callback: (String) -> Unit) {
        val outputReader = InputStreamReader(process.inputStream).buffered()
        outputReader.lineSequence().forEach { line ->
            withContext(Dispatchers.Main) {
                callback(line)
            }
        }
    }

    private suspend fun readProcessErrors(process: Process, callback: (String) -> Unit) {
        val errorReader = InputStreamReader(process.errorStream).buffered()
        errorReader.lineSequence().forEach { line ->
            withContext(Dispatchers.Main) {
                callback(line)
            }
        }
    }
}
