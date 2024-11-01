package molczane.script.runner.app.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
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
                pushStringAnnotation(tag = "ERROR", annotation = "${error.lineNumber}:${error.columnNumber}")
                withStyle(style = SpanStyle(color = Color.Red, textDecoration = TextDecoration.Underline)) {
                    append(error.message)
                }
                pop()
            }

            Text(
                text = annotatedString,
                fontSize = 14.sp,
                color = Color.Red,
                modifier = Modifier.clickable {
                    // Extract line and column from the annotation and navigate
                    val tag = annotatedString.getStringAnnotations("ERROR", 0, annotatedString.length).firstOrNull()
                    tag?.let {
                        val (line, column) = it.item.split(":").map { num -> num.toInt() }
                        onNavigateToLine(line, column)
                    }
                }
            )
        }
    }
}