package ba.out.bring.hodooMobile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.russhwolf.settings.Settings
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun CurrentBalanceScreen(
    settings: Settings,
    odooRpc: OdooRpc
) {
    var accounts by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }

    LaunchedEffect(Unit) {
        val odooUrl = settings.getString("odooUrl", "")
        val odooDb = settings.getString("odooDb", "")
        val odooUser = settings.getString("odooUser", "")
        val odooApiKey = settings.getString("odooApiKey", "")

        if (odooUrl.isNotBlank() && odooDb.isNotBlank() && odooUser.isNotBlank() && odooApiKey.isNotBlank()) {
            try {
                val url = "$odooUrl/jsonrpc"
                val authArgs = buildJsonArray {
                    add(JsonPrimitive(odooDb))
                    add(JsonPrimitive(odooUser))
                    add(JsonPrimitive(odooApiKey))
                    add(JsonObject(emptyMap()))
                }
                val authResult = odooRpc.call<JsonElement>(url, "common", "authenticate", authArgs)

                val searchReadArgs = buildJsonArray {
                    add(JsonPrimitive(odooDb))
                    add(authResult.result?.jsonObject?.get("uid"))
                    add(JsonPrimitive(odooApiKey))
                    add(JsonPrimitive("account.account"))
                    add(JsonPrimitive("search_read"))
                    add(buildJsonArray {
                        add(buildJsonArray {
                            add(JsonPrimitive("account_type"))
                            add(JsonPrimitive("="))
                            add(JsonPrimitive("asset_cash"))
                        })
                    })
                }
                val searchReadResult = odooRpc.call<JsonArray>(url, "object", "execute", searchReadArgs)

                val fetchedAccounts = searchReadResult.result?.map {
                    val name = it.jsonObject["name"]?.jsonPrimitive?.content ?: ""
                    val balance = it.jsonObject["current_balance"]?.jsonPrimitive?.content?.toDoubleOrNull() ?: 0.0
                    name to balance
                } ?: emptyList()

                accounts = fetchedAccounts

            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("Account Name", modifier = Modifier.weight(1f))
            Text("Current Balance")
        }
        Spacer(modifier = Modifier.height(8.dp))
        accounts.forEach { (name, balance) ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(name, modifier = Modifier.weight(1f))
                Text("$$balance")
            }
        }
    }
}
