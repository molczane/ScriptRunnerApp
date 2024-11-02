package molczane.script.runner.app.view

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun StatusIndicator(isRunning: Boolean, exitCode: Int?) {
    val darkGreen = Color(red = 0, green = 100, blue = 20)

    // Determine the base color of the indicator
    val indicatorColor by animateColorAsState(
        targetValue = when {
            isRunning -> darkGreen // Green when running
            exitCode != null && exitCode == 0 -> Color.Green // Green if completed successfully
            else -> Color.Red // Red for non-zero exit code or when not running
        }
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = if (isRunning) 1f else 0.7f, // Pulsating alpha for a glowing effect
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Box for the glowing indicator
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .alpha(animatedAlpha) // Animate the opacity
            .background(color = indicatorColor)
            .padding(16.dp)
//            .then(modifier),
    )
}
