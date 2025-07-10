package ba.out.bring.hodooMobile

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class JsonRpcRequest(
    val jsonrpc: String = "2.0",
    val method: String = "call",
    val params: JsonObject,
    val id: Int = 1
)

@Serializable
data class JsonRpcResponse<T>(
    val result: T? = null,
    val error: JsonObject? = null
)

class OdooRpc(val client: HttpClient) {
    class OdooRpcException(val error: JsonObject) : Exception("Odoo RPC Error: ${error}")
    suspend inline fun <reified T> call(
        url: String,
        service: String,
        method: String,
        args: JsonArray
    ): JsonRpcResponse<T> {
        val request = JsonRpcRequest(
            params = buildJsonObject {
                put("service", service)
                put("method", method)
                put("args", args)
            }
        )
        val response = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body<JsonRpcResponse<T>>()

        if (response.error != null) {
            throw OdooRpcException(response.error)
        }

        return response
    }

    suspend fun getServerVersion(
        odooUrl: String,
        odooDb: String,
        odooUser: String,
        odooApiKey: String
    ): String? {
        return try {
            val url = "$odooUrl/web/session/authenticate"
            val args = buildJsonObject {
                put("db", odooDb)
                put("login", odooUser)
                put("password", odooApiKey)
            }
            val request = JsonRpcRequest(
                method = "call",
                params = buildJsonObject {
                    put("service", "common")
                    put("method", "login")
                    put("args", buildJsonArray { add(args) })
                }
            )
            val response = client.post(url) {
                contentType(ContentType.Application.Json)
                setBody(request)
            }.body<JsonObject>()

            response["result"]?.jsonObject?.get("server_version")?.jsonPrimitive?.content
        } catch (e: Exception) {
            println("Error getting Odoo server version: ${e.message}")
            null
        }
    }
}

fun createOdooRpcClient(): OdooRpc {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
    }
    return OdooRpc(client)
}
