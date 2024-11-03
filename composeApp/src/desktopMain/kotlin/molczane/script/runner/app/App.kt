package molczane.script.runner.app

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import molczane.script.runner.app.view.ScriptEditor
import molczane.script.runner.app.viewModel.ScriptViewModel
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(viewModel: ScriptViewModel) {
    MaterialTheme {
        ScriptEditor(viewModel = viewModel)
    }
}