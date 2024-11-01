package molczane.script.runner.app

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "ScriptRunnerApp",
    ) {
        App()
    }
}