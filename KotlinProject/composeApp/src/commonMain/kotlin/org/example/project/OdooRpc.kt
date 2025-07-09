package org.example.project

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

class OdooRpc(private val client: HttpClient) {
    class OdooRpcException(val error: JsonObject) : Exception("Odoo RPC Error: ${error}")
    suspend fun <T> call(
        url: String,
        service: String,
        method: String,
        args: JsonObject
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

    suspend fun <T> callCommon(
        url: String,
        method: String,
        db: String,
        loginArgs: JsonObject
    ): JsonRpcResponse<T> {
        val request = JsonRpcRequest(
            params = buildJsonObject {
                put("service", "common")
                put("method", method)
                put("args", buildJsonArray {
                    add(JsonPrimitive(db))
                    add(JsonPrimitive(loginArgs["login"]?.jsonPrimitive?.content))
                    add(JsonPrimitive(loginArgs["password"]?.jsonPrimitive?.content))
                })
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
}

fun createOdooRpcClient(): OdooRpc {
    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true; ignoreUnknownKeys = true })
        }
    }
    return OdooRpc(client)
}
