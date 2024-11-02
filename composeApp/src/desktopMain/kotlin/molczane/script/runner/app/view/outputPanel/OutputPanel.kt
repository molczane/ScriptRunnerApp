package molczane.script.runner.app.view.outputPanel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import molczane.script.runner.app.viewModel.ScriptViewModel

@Composable
fun OutputPanel(viewModel: ScriptViewModel, onNavigateToLine: (Int, Int) -> Unit) {
    Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(8.dp)
        ) {
            Text(
                text = viewModel.outputState.value,
                color = if (viewModel.exitCode.value == 0) Color.Black else Color.White,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
            // Display error output
            ErrorOutput(
                errors = viewModel.errorList,
                onNavigateToLine = onNavigateToLine
            )
        }
}
