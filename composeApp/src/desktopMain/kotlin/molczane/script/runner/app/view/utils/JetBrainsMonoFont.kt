package molczane.script.runner.app.view.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import scriptrunnerapp.composeapp.generated.resources.JetBrainsMonoNL_Bold
import scriptrunnerapp.composeapp.generated.resources.JetBrainsMonoNL_Regular
import scriptrunnerapp.composeapp.generated.resources.Res

@OptIn(ExperimentalResourceApi::class)
@Composable
fun JetBrainsMonoFontFamily() = FontFamily(
    Font(Res.font.JetBrainsMonoNL_Regular, weight = FontWeight.Normal),
    Font(Res.font.JetBrainsMonoNL_Bold, weight = FontWeight.Bold)
)