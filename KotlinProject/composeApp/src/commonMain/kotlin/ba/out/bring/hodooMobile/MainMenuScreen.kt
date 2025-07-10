package ba.out.bring.hodooMobile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MainMenuScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToInfo: () -> Unit,
    onNavigateToCurrentBalance: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onNavigateToSettings,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Settings")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToInfo,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Info")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onNavigateToCurrentBalance,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("Current Balance")
        }
    }
}
