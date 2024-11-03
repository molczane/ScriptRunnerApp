package molczane.script.runner.app

import androidx.compose.runtime.remember
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import molczane.script.runner.app.viewModel.ScriptViewModel
import java.awt.Image
import javax.imageio.ImageIO

fun main() = application {
    val viewModel = remember { ScriptViewModel() }

    // To be deleted
    val iconStream = this::class.java.getResourceAsStream("/icons/AppIcon.icns")
    val iconImage: Image? = iconStream?.let { ImageIO.read(it) }

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
        if (iconImage != null) {
            window.iconImage = iconImage // Assign the loaded icon image
        }

        App(viewModel)
    }
}