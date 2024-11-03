package molczane.script.runner.app.view

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import molczane.script.runner.app.view.utils.RunStopButton
import molczane.script.runner.app.view.codeEditor.CodeEditor
import molczane.script.runner.app.view.codeEditor.LineNumberColumn
import molczane.script.runner.app.view.outputPanel.OutputPanel
import molczane.script.runner.app.view.outputPanel.StatusIndicator
import molczane.script.runner.app.view.utils.LanguageSelector
import molczane.script.runner.app.view.utils.addDarkGrayBorder
import molczane.script.runner.app.viewModel.ScriptViewModel

@Composable
fun ScriptEditor(viewModel: ScriptViewModel) {
    var textFieldValue by remember { mutableStateOf(TextFieldValue(viewModel.scriptState.value.scriptText)) }
    var editorHeight by remember { mutableStateOf(500.dp) } // Initial height for the code editor

    val onNavigateToLine: (Int, Int) -> Unit = { line, column ->
        val textLines = textFieldValue.text.lines()
        if (line <= textLines.size) {
            val offset = textLines.take(line - 1).sumOf { it.length + 1 } + (column - 1)
            textFieldValue = textFieldValue.copy(selection = TextRange(offset, offset + 1))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(43, 45, 48)) // Set the background color for the whole application
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .background(Color.Transparent)
        ) {
            TopAppBar(
                title = {
                    Text(
                        "Script Runner App",
                        fontFamily = FontFamily.Monospace,
                        color = Color(200, 200, 200)
                    )
                },
                actions = {
                    // Language selector at the top
                    LanguageSelector(
                        selectedScriptingLanguage = viewModel.selectedScriptingLanguage.value,
                        onLanguageChange = { language ->
                            viewModel.selectedScriptingLanguage.value = language
                            // Add logic to switch scripting behavior based on the language if needed
                        }
                    )

                    // Spacer to add some space between the LanguageSelector and RunStopButton
                    Spacer(modifier = Modifier.width(8.dp))

                    // Custom RunStopButton positioned in the top-right toolbar
                    RunStopButton(
                        isRunning = viewModel.isRunning.value,
                        onRunClick = { viewModel.runScript() },
                        onStopClick = { viewModel.stopScript() }
                    )
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                modifier = Modifier
                    .padding(8.dp)
            )

            var columnHeight by remember { mutableStateOf(0) } // Store height in pixels

            // Split the code editor and output panel vertically with a draggable handle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        columnHeight = coordinates.size.height
                    }
            ) {
                Column (
                    modifier = Modifier
                        .addDarkGrayBorder()
                ) {
                    val codeEditorScrollState = rememberScrollState(0)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(editorHeight)
                            .background(Color(30, 31, 34))
                            .verticalScroll(codeEditorScrollState)
                            .addDarkGrayBorder()
                    ) {
                        Row(modifier = Modifier
                            .fillMaxWidth()
                        ) {
                            LineNumberColumn(text = textFieldValue.text)
                            CodeEditor(
                                textFieldValue = textFieldValue,
                                onTextChange = {
                                    textFieldValue = it
                                    viewModel.updateScript(it.text)
                                },
                                viewModel = viewModel
                            )
                        }
                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd)
                                .height(editorHeight),
                            //.fillMaxHeight(),
                            adapter = rememberScrollbarAdapter(codeEditorScrollState)
                        )
                    }

                    // Draggable handle for resizing the panels
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .background(Color.Gray)
                            .pointerInput(Unit) {
                                detectVerticalDragGestures { change, dragAmount ->
                                    val dragAmountDp = with(density) { dragAmount.toDp() }
                                    editorHeight =
                                        (editorHeight + dragAmountDp)//.coerceIn(100.dp, 600.dp)
                                    change.consume()
                                }
                            }
                            .pointerHoverIcon(PointerIcon.Hand)
                            .addDarkGrayBorder()
                    )

                    val outputPanelScrollState = rememberScrollState(0)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .weight(1f)
                            .background(Color.Black)
                            .addDarkGrayBorder()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.LightGray)
                                .verticalScroll(outputPanelScrollState)
                        ) {
                            OutputPanel(viewModel = viewModel, onNavigateToLine = onNavigateToLine)
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd) // Aligns the inner Box to the top end
                                .padding(8.dp) // Adds padding around the StatusIndicator
                        ) {
                            StatusIndicator(
                                isRunning = viewModel.isRunning.value,
                                exitCode = viewModel.exitCode.value,
                            )
                        }
                    }
                }
            }
        }
    }
}