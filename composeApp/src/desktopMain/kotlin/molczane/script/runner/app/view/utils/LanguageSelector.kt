package molczane.script.runner.app.view.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import molczane.script.runner.app.model.ScriptingLanguage

@Composable
fun LanguageSelector(
    selectedScriptingLanguage: ScriptingLanguage,
    onLanguageChange: (ScriptingLanguage) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .background(Color.Transparent)
            .height(40.dp)
            .width(200.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { onLanguageChange(ScriptingLanguage.Kotlin) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (selectedScriptingLanguage == ScriptingLanguage.Kotlin) Color(68, 126, 181) else Color.Gray,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(
                topStart = 8.dp,
                bottomStart = 8.dp,
                topEnd = 0.dp, // Adjust this value to your desired roundness
                bottomEnd = 0.dp // Adjust this value to your desired roundness
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text("Kotlin")
        }
        Button(
            onClick = { onLanguageChange(ScriptingLanguage.Swift) },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = if (selectedScriptingLanguage == ScriptingLanguage.Swift) Color(68, 126, 181) else Color.Gray,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 0.dp,
                topEnd = 8.dp, // Adjust this value to your desired roundness
                bottomEnd = 8.dp // Adjust this value to your desired roundness
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text("Swift")
        }
    }
}
