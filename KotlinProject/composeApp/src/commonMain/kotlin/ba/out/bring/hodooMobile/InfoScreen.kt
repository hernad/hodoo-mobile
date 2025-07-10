package ba.out.bring.hodooMobile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InfoScreen(
    odooRpc: OdooRpc,
    odooUrl: String,
    odooDb: String,
    odooUser: String,
    odooApiKey: String
) {
    var odooVersion by remember { mutableStateOf("Loading...") }

    LaunchedEffect(odooUrl, odooDb, odooUser, odooApiKey) {
        val version = odooRpc.getServerVersion(odooUrl, odooDb, odooUser, odooApiKey)
        odooVersion = version ?: "Failed to get version"
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Info Screen")
        Spacer(modifier = Modifier.height(16.dp))
        Text("Odoo Server Version: $odooVersion")
    }
}
