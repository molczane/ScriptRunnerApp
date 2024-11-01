package molczane.script.runner.app

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ScriptRunnerApp",
        state = WindowState(size = DpSize.Unspecified)
    ) {
        App()
    }
}