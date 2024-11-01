package molczane.script.runner.app.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import molczane.script.runner.app.viewModel.ScriptViewModel

@Composable
fun ScriptEditor(viewModel: ScriptViewModel) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(viewModel.scriptState.value.scriptText)) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Edytor kodu
//        BasicTextField(
//            value = textFieldValue,
//            onValueChange = { newValue ->
//                textFieldValue = newValue
//                viewModel.updateScript(newValue.text)
//            },
//            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp)
//                .background(Color.LightGray),
//            decorationBox = { innerTextField ->
//                Box(modifier = Modifier.fillMaxWidth()) {
//                    // Build an annotated string for the text field with highlighting
//                    val highlightedText = viewModel.highlightSyntax()
//                    val annotatedString = buildAnnotatedString {
//                        highlightedText.forEach { (word, isKeyword) ->
//                            if (isKeyword) {
//                                withStyle(style = SpanStyle(color = Color.Blue)) {
//                                    append(word)
//                                }
//                            } else {
//                                append(word)
//                            }
//                        }
//                    }
//
//                    // Draw the highlighted text
//                    innerTextField()
//                    if (textFieldValue.text.isNotEmpty()) {
//                        // Display the text with syntax highlighting
//                        BasicTextField(
//                            value = textFieldValue.copy(annotatedString = annotatedString),
//                            onValueChange = { newValue ->
//                                textFieldValue = newValue
//                                viewModel.updateScript(newValue.text)
//                            },
//                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 16.sp),
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                    }
//                }
//            }
//        )
        CodeEditorWithLineNumbers(viewModel = viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Przycisk Run Script
        Button(
            onClick = {
                viewModel.runScript()
            },
            enabled = !viewModel.isRunning.value,
        ) {
            Text("Run Script")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Panel wyjściowy
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.LightGray)
                .padding(8.dp)
        ) {
            Text(
                text = viewModel.outputState.value,
                color = if (viewModel.exitCode.value == 0) Color.Black else Color.Red,
                fontSize = 14.sp
            )
        }
    }
}
