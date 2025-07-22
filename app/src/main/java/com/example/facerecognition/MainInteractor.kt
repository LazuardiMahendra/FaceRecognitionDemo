package com.example.facerecognition

import android.content.Context
import android.net.Uri
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

class MainInteractor(_presenter: MainPresenterInterface, val context: Context) :
    MainInteractorInterface {

    private var presenter: MainPresenterInterface = _presenter
    private var network: MainNetwork? = null


    init {
        network = MainNetwork(this, context)
    }

    override fun detectFace1(face1: Uri) {
        network?.detectFace1(face1)
    }

    override fun detectFace2(byteArray: ByteArray) {
        network?.detectFace2(byteArray)
    }

    override fun downloadImage(linkImage: String) {
        network?.downloadImage(linkImage)
    }

    override fun verifyFace(faceId1: String, faceId2: String) {
        network?.verifyFace(faceId1, faceId2)
    }

    override fun parseDetectFace1(obj: JSONArray) {
        presenter.parseDetectFace1(obj)
    }

    override fun parseDetectFace2(obj: JSONArray) {
        presenter.parseDetectFace2(obj)
    }

    override fun parseDownloadImage(inputStream: InputStream) {
        presenter.parseDownloadImage(inputStream)
    }

    override fun parseVerifyFace(obj: JSONObject) {
        presenter.parseVerifyFace(obj)
    }
}