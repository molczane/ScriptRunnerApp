package molczane.script.runner.app.view.codeEditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import molczane.script.runner.app.viewModel.ScriptViewModel
import molczane.script.runner.app.view.utils.JetBrainsMonoFontFamily

@Composable
fun CodeEditor(
    textFieldValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    viewModel: ScriptViewModel
) {
    val textLines = textFieldValue.text.split("\n")

    // Determine the current line index based on the cursor position


    Box(modifier = Modifier
        .fillMaxWidth()) {
        BasicTextField(
            value = textFieldValue,
            onValueChange = onTextChange,
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = JetBrainsMonoFontFamily()),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color.Transparent),
            cursorBrush = SolidColor(Color.White),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    val highlightedText = viewModel.highlightSyntax()

                    val annotatedString = buildAnnotatedString {
                        highlightedText.forEach { (word, isKeyword) ->
                            if (isKeyword) {
                                withStyle(style = SpanStyle(color = Color(68, 126, 181))) {
                                    append(word)
                                }
                            } else {
                                withStyle(style = SpanStyle(color = Color(200, 200, 200))) {
                                    append(word)
                                }
                            }
                        }
                    }

                    innerTextField()
                    if (textFieldValue.text.isNotEmpty()) {
                        BasicTextField(
                            value = textFieldValue.copy(annotatedString = annotatedString),
                            onValueChange = onTextChange,
                            textStyle = TextStyle(fontSize = 16.sp, fontFamily = JetBrainsMonoFontFamily()),
                            modifier = Modifier.fillMaxWidth(),
                            cursorBrush = SolidColor(Color.White),
                        )
                    }
                }
            }
        )
    }
}
