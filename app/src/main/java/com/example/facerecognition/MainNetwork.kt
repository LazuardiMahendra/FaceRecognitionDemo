package com.example.facerecognition

import android.content.Context
import android.net.Uri
import android.util.Log
import com.android.volley.AuthFailureError
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.nio.charset.StandardCharsets
import kotlin.jvm.Throws

class MainNetwork(_model: MainInteractorInterface, val context: Context) : MainNetworkInterface {

    private val model: MainInteractorInterface = _model

    private var getDetectFace1Queue: RequestQueue? = null
    private var getDetectFace2Queue: RequestQueue? = null
    private var downloadImageQueue: RequestQueue? = null
    private var getVerifyFaceQueue: RequestQueue? = null

    override fun detectFace1(face1: Uri) {
        val url = Constant.ENDPOINT + Constant.DETECT_FACE
        getDetectFace1Queue = Volley.newRequestQueue(context)

        val uploadRequest =
            object : VolleyByteArrayRequest(Method.POST, url, Response.Listener { response ->
                try {
                    val json = String(response.data, StandardCharsets.UTF_8)
                    val obj = JSONArray(json)

                    model.parseDetectFace1(obj)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { volleyError ->
                try {
                    if (volleyError.networkResponse.statusCode == 401) {
                        cancelAllRequest()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()

                }
            }) {

                override fun getBody(): ByteArray? = uriToByteArray(context, face1)


                override fun getHeaders(): MutableMap<String, String> = mutableMapOf(
                    "Ocp-Apim-Subscription-Key" to Constant.API_KEY,
                    "Content-Type" to "application/octet-stream"
                )

            }

        RetryPolicy().setRetryPolicyByteArrayUploadRequest(uploadRequest)
        getDetectFace1Queue?.add(uploadRequest)
    }

    override fun downloadImage(linkImage: String) {
        downloadImageQueue?.cancelAll { true }
        println("downloadPdf() URL : $linkImage")
        val body = JSONObject()

        downloadImageQueue = Volley.newRequestQueue(context)

        val inputStreamRequest = object : InputStreamVolleyRequest(
            Method.GET, linkImage, body,
            Response.Listener { inputStream ->
                println("downloadPdf() RESPONSE : $inputStream")
                try {
                    model.parseDownloadImage(inputStream)
                } catch (e: Exception) {
                }
            },
            Response.ErrorListener { volleyError ->
                println("downloadPdf() ERROR : ${volleyError.message}")
                try {
                    if (volleyError.networkResponse.statusCode == 401) {
//                        cancelAllRequest()
                    }
                } catch (_: Exception) {
                }
            }) {

            override fun getHeaders(): MutableMap<String, String>? {
                return VolleyRequest(context).headerSecureWithoutToken(body)
            }
        }

        RetryPolicy().setRetryInputStreamRequest(inputStreamRequest)
        downloadImageQueue?.add(inputStreamRequest)
    }

    override fun detectFace2(dataByteArray: ByteArray) {
        val url = Constant.ENDPOINT + Constant.DETECT_FACE
        getDetectFace2Queue = Volley.newRequestQueue(context)

        val uploadRequest =
            object : VolleyByteArrayRequest(Method.POST, url, Response.Listener { response ->
                try {
                    val json = String(response.data, StandardCharsets.UTF_8)
                    val obj = JSONArray(json)

                    model.parseDetectFace2(obj)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { volleyError ->
                try {
                    if (volleyError.networkResponse.statusCode == 401) {
                        cancelAllRequest()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.d("face2", "Error $e")

                }
            }) {

                override fun getBody(): ByteArray? = dataByteArray


                override fun getHeaders(): MutableMap<String, String> = mutableMapOf(
                    "Ocp-Apim-Subscription-Key" to Constant.API_KEY,
                    "Content-Type" to "application/octet-stream"
                )

            }

        RetryPolicy().setRetryPolicyByteArrayUploadRequest(uploadRequest)
        getDetectFace2Queue?.add(uploadRequest)
    }

    override fun verifyFace(faceId1: String, faceId2: String) {
        val url = Constant.ENDPOINT + Constant.VERIFY_FACE
        getVerifyFaceQueue = Volley.newRequestQueue(context)

        val body = JSONObject()
        body.put("faceId1", faceId1)
        body.put("faceId2", faceId2)

        val jsonObjectRequest =
            object : JsonObjectRequest(url, body, Response.Listener<JSONObject> { response ->
                try {
                    Log.d("verifyFace", response.toString())
                    model.parseVerifyFace(response)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { volleyError ->
                try {
                    if (volleyError.networkResponse.statusCode == 401) {
                        cancelAllRequest()
                    }
                } catch (e: Exception) {
                    Log.d("verifyFace", "$e")

                }
            }) {
                override fun getHeaders(): MutableMap<String, String> = mutableMapOf(
                    "Ocp-Apim-Subscription-Key" to Constant.API_KEY,
                )
            }

        RetryPolicy().setRetryPolicyJsonObjectRequest(jsonObjectRequest)
        getVerifyFaceQueue?.add(jsonObjectRequest)
    }

    private fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                inputStream.readBytes()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun cancelAllRequest() {
        getDetectFace1Queue?.cancelAll { true }
        getDetectFace2Queue?.cancelAll { true }
        downloadImageQueue?.cancelAll { true }
        getVerifyFaceQueue?.cancelAll { true }
    }

}