package com.example.facerecognition

import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest

class RetryPolicy {

    fun setRetryPolicyJsonObjectRequest(jsonObjectRequest: JsonObjectRequest) {
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            500000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    fun setRetryPolicyUploadRequest(uploadRequest: VolleyFileUploadRequest) {
        uploadRequest.retryPolicy = DefaultRetryPolicy(
            500000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    fun setRetryPolicyByteArrayUploadRequest(byteArrayRequest: VolleyByteArrayRequest) {
        byteArrayRequest.retryPolicy = DefaultRetryPolicy(
            500000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }

    fun setRetryInputStreamRequest(inputStreamRequest: InputStreamVolleyRequest) {
        inputStreamRequest.retryPolicy = DefaultRetryPolicy(
            500000,
            0,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
    }
}