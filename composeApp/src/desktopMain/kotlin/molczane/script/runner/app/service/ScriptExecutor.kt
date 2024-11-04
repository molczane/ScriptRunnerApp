package molczane.script.runner.app.service

import java.io.File

interface ScriptExecutor {
    fun prepareScriptFile(scriptContent: String): File
    fun getProcessBuilder(scriptFile: File): ProcessBuilder
}
