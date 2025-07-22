package com.example.facerecognition

import com.android.volley.NetworkResponse
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser

open class VolleyByteArrayRequest(
    method: Int,
    url: String,
    listener: Response.Listener<NetworkResponse>,
    errorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, errorListener) {

    private var responseListener: Response.Listener<NetworkResponse>? = null

    init {
        this.responseListener = listener
    }

    override fun getBodyContentType(): String = "application/octet-stream"

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return try {
            Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(ParseError(e))
        }
    }

    override fun deliverResponse(response: NetworkResponse) {
        responseListener?.onResponse(response)
    }

    override fun deliverError(error: VolleyError) {
        errorListener?.onErrorResponse(error)
    }

}