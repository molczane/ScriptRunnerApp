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
}