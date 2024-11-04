package molczane.script.runner.app

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import molczane.script.runner.app.viewModel.ScriptViewModel

fun main() = application {
    val viewModel = remember { ScriptViewModel() }

    Window(
        onCloseRequest = {
            viewModel.stopScript()

            try {
                viewModel.fileToDestroy!!.delete()
            }
            catch (e: Exception) {
                println(e.message)
            }

            // Exit the application
            exitApplication()
        },
        title = "ScriptRunnerApp",
        state = WindowState(size = DpSize.Unspecified)
    ) {
        App(viewModel)
    }
}