package com.example.facerecognition

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream

class ImageHelper {

    private fun getFileDataFromDrawable(bitmap: Bitmap): ByteArray? {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream)
        return byteArrayOutputStream.toByteArray()
    }

    //=== Function for resized Image ===//
    private fun getResizedBitmap(image: Bitmap?, maxSize: Int): Bitmap {
        var width = image!!.width
        var height = image.height

        return if (width <= maxSize && height <= maxSize) {
            Bitmap.createScaledBitmap(image, width, height, true)
        } else {
            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }
            Bitmap.createScaledBitmap(image, width, height, true)
        }
    }

    fun getDataFromUri(context: Context, uri: String): ByteArray? {
        val imageStream = context.contentResolver.openInputStream(Uri.parse(uri))
        val selectedImage = BitmapFactory.decodeStream(imageStream)
        val resImage = ImageHelper().getResizedBitmap(selectedImage, 1000)

        return resImage.let { ImageHelper().getFileDataFromDrawable(it) }
    }

}