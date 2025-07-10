package ba.out.bring.hodooMobile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import ba.out.bring.hodooMobile.createSettings
import org.jetbrains.compose.ui.tooling.preview.Preview

enum class Screen {
    MainMenu, Settings, Info, CurrentBalance
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun App(context: Any? = null) {
    MaterialTheme {
        val settings = remember { createSettings(context) }
        val odooUrl by remember {
            mutableStateOf(
                settings.getString(
                    "odooUrl",
                    "http://192.168.168.148:8069"
                )
            )
        }
        val odooDb by remember { mutableStateOf(settings.getString("odooDb", "odoo-test-1")) }
        val odooUser by remember { mutableStateOf(settings.getString("odooUser", "admin")) }
        val odooApiKey by remember { mutableStateOf(settings.getString("odooApiKey", "admin")) }
        val odooRpc = remember { createOdooRpcClient() }
        val snackbarHostState = remember { SnackbarHostState() }
        var currentScreen by remember { mutableStateOf(Screen.MainMenu) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Hodoo Mobile") },
                    navigationIcon = {
                        if (currentScreen != Screen.MainMenu) {
                            IconButton(onClick = { currentScreen = Screen.MainMenu }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                when (currentScreen) {
                    Screen.MainMenu -> MainMenuScreen(
                        onNavigateToSettings = { currentScreen = Screen.Settings },
                        onNavigateToInfo = { currentScreen = Screen.Info },
                        onNavigateToCurrentBalance = { currentScreen = Screen.CurrentBalance }
                    )
                    Screen.Settings -> SettingsScreen(
                        settings = settings,
                        odooRpc = odooRpc,
                        snackbarHostState = snackbarHostState
                    )
                    Screen.Info -> InfoScreen(
                        odooRpc = odooRpc,
                        odooUrl = odooUrl,
                        odooDb = odooDb,
                        odooUser = odooUser,
                        odooApiKey = odooApiKey
                    )
                    Screen.CurrentBalance -> CurrentBalanceScreen(
                        settings = settings,
                        odooRpc = odooRpc,
                        snackbarHostState = snackbarHostState
                    )
                }
            }
        }
    }
}
