package ba.out.bring.hodooMobile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SnackbarHostState
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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import androidx.compose.runtime.rememberCoroutineScope


@Composable
fun CurrentBalanceScreen(
    settings: Settings,
    odooRpc: OdooRpc,
    snackbarHostState: SnackbarHostState
) {
    var accounts by remember { mutableStateOf<List<Pair<String, Double>>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val odooUrl = settings.getString("odooUrl", "")
        val odooDb = settings.getString("odooDb", "")
        val odooUser = settings.getString("odooUser", "")
        val odooApiKey = settings.getString("odooApiKey", "")


        if (odooUrl.isNotBlank() && odooDb.isNotBlank() && odooUser.isNotBlank() && odooApiKey.isNotBlank()) {

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
                    val uid = result.result
                    if ((uid as? JsonPrimitive)?.intOrNull?.let { it >= 1 && it <= 9999 } ?: false) {
                        val searchReadArgs = buildJsonArray {
                            add(JsonPrimitive(odooDb))
                            add(uid)
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
                    } else {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Odoo RPC Error!")
                        }
                    }

                } catch (e: OdooRpc.OdooRpcException) {
                    println("Odoo RPC Exception: ${e.error}")
                } catch (e: Exception) {
                    println("General Exception: ${e.message}")
                } finally {
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
