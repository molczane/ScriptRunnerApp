package molczane.script.runner.app.view.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier

// Function to add dark gray border lines
fun Modifier.addDarkGrayBorder(thickness: Dp = 1.dp, color: Color = Color.DarkGray/*Color(60, 60, 60)*/) = this.border(
    BorderStroke(thickness, color),
    shape = RoundedCornerShape(0.dp) // Optional, set shape to 0.dp for sharp edges
)