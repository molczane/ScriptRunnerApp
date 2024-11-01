package molczane.script.runner.app.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import molczane.script.runner.app.viewModel.ScriptViewModel

@Composable
fun ScriptEditor(viewModel: ScriptViewModel) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(viewModel.scriptState.value.scriptText)) }

    val onNavigateToLine: (Int, Int) -> Unit = { line, column ->
        val textLines = textFieldValue.text.lines()
        if (line <= textLines.size) {
            val offset = textLines.take(line - 1).sumOf { it.length + 1 } + (column - 1)
            textFieldValue = textFieldValue.copy(selection = TextRange(offset, offset + 1))
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().background(Color.LightGray)) {
            LineNumberColumn(text = textFieldValue.text)
            CodeEditor(
                textFieldValue = textFieldValue,
                onTextChange = {
                    textFieldValue = it
                    viewModel.updateScript(it.text)
                },
                viewModel = viewModel
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.runScript()
            },
            enabled = !viewModel.isRunning.value,
        ) {
            Text("Run Script")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutputPanel(viewModel = viewModel, onNavigateToLine = onNavigateToLine)
    }
}

