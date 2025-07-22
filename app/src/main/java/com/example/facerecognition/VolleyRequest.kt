package com.example.facerecognition

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.util.HashMap


class VolleyRequest(context: Context) {

    fun headerDetectFace(): MutableMap<String, String>? {
        val params = HashMap<String, String>()
        params["Ocp-Apim-Subscription-Key"] = Constant.API_KEY
        params["Content-Type"] = "application/json"
        return params
    }

    fun headerSecureWithoutToken(body: JSONObject): MutableMap<String, String>? {
        Log.d("SIGNATURE BODY", body.toString())

        return HashMap()
    }

    fun bodySecure(params: JSONObject): JSONObject {
        Log.d("BODY", params.toString())

        return params
    }

}