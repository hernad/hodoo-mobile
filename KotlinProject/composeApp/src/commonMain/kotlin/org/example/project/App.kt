package org.example.project

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App() {
    MaterialTheme {
        var odooUrl by remember { mutableStateOf("http://localhost:8069") }
        var odooDb by remember { mutableStateOf("odoo-test-1") }
        var odooUser by remember { mutableStateOf("admin") }
        var odooApiKey by remember { mutableStateOf("admin") }
        var checkState by remember { mutableStateOf<Boolean?>(null) }
        var isLoading by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        val odooRpc = remember { createOdooRpcClient() }

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
            OutlinedTextField(
                value = odooApiKey,
                onValueChange = { odooApiKey = it },
                label = { Text("Odoo API Key") },
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
                            val params = buildJsonObject {
                                put("db", odooDb)
                                put("login", odooUser)
                                put("password", odooApiKey)
                            }
                            val result = odooRpc.call<Int>(url, "common", "login", params)
                            checkState = result.result != null
                            isLoading = false
                        }
                    },
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
                        Text("Provjeri")
                    }
                }
            }
            if (checkState == false) {
                Text(
                    text = "Error connecting to Odoo. Please check your settings.",
                    color = Color.Red,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
