package ba.out.bring.hodooMobile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.put

@Composable
fun SettingsScreen(
    settings: Settings,
    odooRpc: OdooRpc,
    snackbarHostState: SnackbarHostState
) {
    var odooUrl by remember {
        mutableStateOf(
            settings.getString(
                "odooUrl",
                "http://192.168.168.148:8069"
            )
        )
    }
    var odooDb by remember { mutableStateOf(settings.getString("odooDb", "odoo-test-1")) }
    var odooUser by remember { mutableStateOf(settings.getString("odooUser", "admin")) }
    var odooApiKey by remember { mutableStateOf(settings.getString("odooApiKey", "admin")) }
    var checkState by remember { mutableStateOf<Boolean?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .safeContentPadding()
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = odooUrl,
            onValueChange = { odooUrl = it },
            label = { Text("Odoo URL") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = odooDb,
            onValueChange = { odooDb = it },
            label = { Text("Odoo Database") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = odooUser,
            onValueChange = { odooUser = it },
            label = { Text("Odoo User") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = odooApiKey,
            onValueChange = { odooApiKey = it },
            label = { Text("Odoo API Key") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible)
                    Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff

                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Button(onClick = {
                // TODO: Implement QR code scanner
            }) {
                Text("Scan QR")
            }
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        val url = "$odooUrl/jsonrpc"
                        val args = buildJsonArray {
                            add(JsonPrimitive(odooDb))
                            add(JsonPrimitive(odooUser))
                            add(JsonPrimitive(odooApiKey))
                        }
                        try {
                            val result =
                                odooRpc.call<JsonElement>(
                                    url,
                                    "common",
                                    "login",
                                    args
                                )
                            println("Odoo RPC Login Raw Result: $result")
                            println("Odoo RPC Login Result.result: ${result.result}")
                            checkState =
                                (result.result as? JsonPrimitive)?.intOrNull?.let { it >= 1 && it <= 999 }
                                    ?: false
                        } catch (e: OdooRpc.OdooRpcException) {
                            println("Odoo RPC Exception: ${e.error}")
                            checkState = false
                        } catch (e: Exception) {
                            println("General Exception: ${e.message}")
                            checkState = false
                        } finally {
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = when (checkState) {
                        true -> Color.Green
                        false -> Color.Red
                        else -> MaterialTheme.colorScheme.primary
                    }
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White)
                } else {
                    Text("Check configuration")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        var internetCheckState by remember { mutableStateOf<Boolean?>(null) }
        var internetLoading by remember { mutableStateOf(false) }
        Button(
            onClick = {
                coroutineScope.launch {
                    internetLoading = true
                    try {
                        val httpClient =
                            HttpClient() // Create a new HttpClient for internet check
                        val response = httpClient.get("https://www.google.com")
                        internetCheckState = response.status.value == 200
                        httpClient.close()
                    } catch (e: Exception) {
                        println("Internet check exception: ${e.message}")
                        internetCheckState = false
                    } finally {
                        internetLoading = false
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = when (internetCheckState) {
                    true -> Color.Green
                    false -> Color.Red
                    else -> MaterialTheme.colorScheme.primary
                }
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (internetLoading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("Check Internet")
            }
        }
        if (internetCheckState == false) {
            Text(
                text = "No internet connection. Please check your network.",
                color = Color.Red,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            settings["odooUrl"] = odooUrl
            settings["odooDb"] = odooDb
            settings["odooUser"] = odooUser
            settings["odooApiKey"] = odooApiKey
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Configuration saved!")
            }
        }) {
            Text("Save")
        }
    }
}
