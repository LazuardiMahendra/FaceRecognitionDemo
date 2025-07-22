package com.example.facerecognition

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import org.json.JSONObject
import java.io.ByteArrayInputStream
import java.io.InputStream

open class InputStreamVolleyRequest(
    method: Int,
    url: String,
    private val jsonRequest: JSONObject?,
    private val responseListener: Response.Listener<InputStream>,
    errorListener: Response.ErrorListener
) : Request<InputStream>(method, url, errorListener) {
    private var headers: Map<String, String>? = null

    override fun getHeaders(): MutableMap<String, String>? =
        when(headers) {
            null -> super.getHeaders()
            else -> headers!!.toMutableMap()
        }

    override fun getBody(): ByteArray? {
        return jsonRequest?.toString()?.toByteArray(Charsets.UTF_8)
    }

    override fun getBodyContentType(): String {
        return "application/json; charset=utf-8"
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<InputStream> {
        return try {
            val inputStream = ByteArrayInputStream(response.data)
            Response.success(inputStream, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: InputStream) {
        responseListener.onResponse(response)
    }

    override fun deliverError(error: VolleyError) {
        errorListener?.onErrorResponse(error)
    }
}