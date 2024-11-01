package molczane.script.runner.app.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LineNumberColumn(text: String) {
    BasicTextField(
        value = buildAnnotatedString {
            val lineCount = text.lines().size
            for (i in 1..lineCount) {
                append("$i\n")
            }
        }.toString(), // Use string representation
        onValueChange = {}, // No-op to make it non-editable
        readOnly = true,
        textStyle = TextStyle(fontSize = 16.sp, color = Color.Gray, fontFamily = FontFamily.Monospace),
        modifier = Modifier
            .background(Color.LightGray)
            .padding(16.dp)
            .width(40.dp) // Adjust width as needed
    )
}
