package molczane.script.runner.app.service

import java.io.File

class KotlinScriptExecutor : ScriptExecutor {
    override fun prepareScriptFile(scriptContent: String): File {
        val tempFile = File("foo.kts")
        tempFile.writeText(scriptContent)
        return tempFile
    }

    override fun getProcessBuilder(scriptFile: File): ProcessBuilder {
        return ProcessBuilder("kotlinc", "-script", scriptFile.absolutePath)
    }
}
