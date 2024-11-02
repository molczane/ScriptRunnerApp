package archive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import molczane.script.runner.app.viewModel.ScriptViewModel

@Composable
fun CodeEditorWithLineNumbers(viewModel: ScriptViewModel) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(viewModel.scriptState.value.scriptText)) }

//    val onNavigateToLine: (Int, Int) -> Unit = { line, column ->
//        // Logic to navigate to the specified line and column
//        // This could involve setting the cursor position in the `TextFieldValue`
//        val textLines = textFieldValue.text.lines()
//        if (line <= textLines.size) {
//            val offset = textLines.take(line - 1).sumOf { it.length + 1 } + (column - 1)
//            textFieldValue = textFieldValue.copy(selection = TextRange(offset, offset + 1))
//        }
//    }

    Row(modifier = Modifier.fillMaxWidth().background(Color.LightGray)) {
        // Line numbers column
        BasicTextField(
            value = buildAnnotatedString {
                val lineCount = textFieldValue.text.lines().size
                for (i in 1..lineCount) {
                    append("$i\n")
                }
            }.toString(), // Use string representation
            onValueChange = {}, // No-op to make it non-editable
            readOnly = true,
            textStyle = TextStyle(fontSize = 16.sp, color = Color.Gray),
            modifier = Modifier
                .background(Color.LightGray)
                .padding(16.dp)
                .width(40.dp) // Adjust width as needed
        )

        // Code editor column
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = { newValue ->
                    textFieldValue = newValue
                    viewModel.updateScript(newValue.text)
                },
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.LightGray),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        // Build an annotated string for the text field with highlighting
                        val highlightedText = viewModel.highlightSyntax()
                        val annotatedString = buildAnnotatedString {
                            highlightedText.forEach { (word, isKeyword) ->
                                if (isKeyword) {
                                    withStyle(style = SpanStyle(color = Color.Blue)) {
                                        append(word)
                                    }
                                } else {
                                    append(word)
                                }
                            }
                        }

                        // Draw the highlighted text
                        innerTextField()
                        if (textFieldValue.text.isNotEmpty()) {
                            // Display the text with syntax highlighting
                            BasicTextField(
                                value = textFieldValue.copy(annotatedString = annotatedString),
                                onValueChange = { newValue ->
                                    textFieldValue = newValue
                                    viewModel.updateScript(newValue.text)
                                },
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            )
        }
    }
}
