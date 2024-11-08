package molczane.script.runner.app.view.outputPanel

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import molczane.script.runner.app.model.ErrorData

@Composable
fun ErrorOutput(errors: List<ErrorData>, onNavigateToLine: (Int, Int) -> Unit) {
    Column(
        modifier = Modifier
            .height(100.dp)
    ) {
        for (error in errors) {
            val annotatedString = buildAnnotatedString {
                if (error.isClickable) {
                    pushStringAnnotation(
                        tag = "ERROR",
                        annotation = "${error.lineNumber}:${error.columnNumber}"
                    )
                    withStyle(
                        style = SpanStyle(
                            color = Color(255, 0, 0),
                            textDecoration = TextDecoration.Underline
                        )
                    ) {
                        append(error.message)
                    }
                    pop()
                } else {
                    append(error.message)
                }
            }

            Text(
                text = annotatedString,
                fontSize = 14.sp,
                color = if (error.isClickable) Color.Red else Color.Transparent,
                fontFamily = FontFamily.Monospace,
                modifier = if (error.isClickable) {
                    Modifier.clickable {
                        val tag = annotatedString.getStringAnnotations(
                            tag = "ERROR",
                            start = 0,
                            end = annotatedString.length
                        ).firstOrNull()

                        tag?.let {
                            val (line, column) = it.item.split(":").map { num -> num.toInt() }
                            onNavigateToLine(line, column)
                        }
                    }
                } else {
                    Modifier
                }
            )
        }
    }
}