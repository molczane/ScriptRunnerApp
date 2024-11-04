package molczane.script.runner.app.view.codeEditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import molczane.script.runner.app.utils.ScriptingLanguage
import molczane.script.runner.app.viewModel.ScriptViewModel
import molczane.script.runner.app.view.utils.JetBrainsMonoFontFamily

@Composable
fun CodeEditor(
    textFieldValue: TextFieldValue,
    onTextChange: (TextFieldValue) -> Unit,
    viewModel: ScriptViewModel
) {
    val cursorPosition = textFieldValue.selection.start
    val textLines = textFieldValue.text.split("\n")

    // Determine the current line index based on the cursor position
    val currentLineIndex = textLines.foldIndexed(0) { index, acc, line ->
        if (acc + line.length + 1 > cursorPosition) {
            return@foldIndexed index
        }
        acc + line.length + 1
    }

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
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    val highlightedText = when (viewModel.selectedScriptingLanguage.value) {
                        ScriptingLanguage.Kotlin -> {
                            viewModel.highlightKotlinSyntax()
                        }

                        ScriptingLanguage.Swift -> {
                            viewModel.highlightSwiftSyntax()
                        }
                    }
                    //val highlightedText = viewModel.highlightKotlinSyntax()
                    val annotatedString = buildAnnotatedString {
                        highlightedText.forEach { (word, isKeyword) ->
                            if (isKeyword) {
                                withStyle(style = SpanStyle(color = Color(68, 126, 181))) {
                                    append(word)
                                }
                            } else {
                                withStyle(style = SpanStyle(color = Color(141, 142, 147))) {
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
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        )
    }
}
