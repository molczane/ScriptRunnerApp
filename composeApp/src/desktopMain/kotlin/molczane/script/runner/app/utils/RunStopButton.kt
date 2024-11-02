package molczane.script.runner.app.utils

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape

@Composable
fun RunStopButton(isRunning: Boolean, onRunClick: () -> Unit, onStopClick: () -> Unit) {
    Button(
        onClick = {
            if (isRunning) {
                onStopClick()
            } else {
                onRunClick()
            }
        },
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Gray // Change color based on `isRunning`
        ),
        modifier = Modifier
            .height(50.dp)
            .width(70.dp)
            .background(Color.LightGray),
        content = {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    //.background(if (isRunning) Color.Red else Color.Green)
                    .clip(if (isRunning) RectangleShape else CircleShape)
            ) {
                // Draw the play triangle or stop square based on the `isRunning` state
                if (isRunning) {
                    // Red stop button (square)
                    Canvas(modifier = Modifier
                        .height(24.dp)
                        .width(30.dp)
                    ) {
                        drawStopIcon()
                    }
                } else {
                    // Green play button (triangle)
                    Canvas(modifier = Modifier.size(24.dp)) {
                        drawPlayIcon()
                    }
                }
            }
        }
    )
}

// Custom function to draw a play triangle
fun DrawScope.drawPlayIcon() {
    val path = Path().apply {
        moveTo(size.width * 0.1f, size.height * 0.1f) // Start at the top left corner (with a small margin)
        lineTo(size.width * 0.9f, size.height / 2)   // Draw to the middle right
        lineTo(size.width * 0.1f, size.height * 0.9f) // Draw to the bottom left corner (with a small margin)
        close() // Close the path to complete the triangle
    }
    drawIntoCanvas { canvas ->
        canvas.drawPath(path, Paint().apply { color = Color.Green })
    }
}

fun DrawScope.drawStopIcon() {
    drawRoundRect(
        color = Color.Red,
        size = Size(24.dp.toPx(), 24.dp.toPx()),
        // Use the full size of the canvas for the square
        cornerRadius = CornerRadius(size.minDimension * 0.1f) // Rounded corners, adjust 0.1f for radius size
    )
}
