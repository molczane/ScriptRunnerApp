package molczane.script.runner.app.service

import java.io.File

class SwiftScriptExecutor : ScriptExecutor {
    override fun prepareScriptFile(scriptContent: String): File {
        val tempFile = File("foo.swift")
        tempFile.writeText(scriptContent)
        return tempFile
    }

    override fun getProcessBuilder(scriptFile: File): ProcessBuilder {
        return ProcessBuilder("/usr/bin/env", "swift", scriptFile.absolutePath)
    }
}
