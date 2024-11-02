package molczane.script.runner.app.view

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import molczane.script.runner.app.utils.RunStopButton
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Top-center "Run Script" button
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            RunStopButton(
                isRunning = viewModel.isRunning.value,
                onRunClick = { viewModel.runScript() },
                onStopClick = { viewModel.stopScript() }
            )
        }

        var columnHeight by remember { mutableStateOf(0) } // Store height in pixels

        // Split the code editor and output panel vertically with a draggable handle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    columnHeight = coordinates.size.height
                }
        ) {
            Column {
                val codeEditorScrollState = rememberScrollState(0)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(editorHeight)
                        .background(Color.LightGray)
                        .verticalScroll(codeEditorScrollState)
                ) {
                    Row(modifier = Modifier.fillMaxWidth()) {
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
                                editorHeight = (editorHeight + dragAmountDp)//.coerceIn(100.dp, 600.dp)
                                change.consume()
                            }
                        }
                        .pointerHoverIcon(PointerIcon.Hand)
                )

                val outputPanelScrollState = rememberScrollState(0)

                Box(modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .weight(1f)
                    .background(Color.LightGray)
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