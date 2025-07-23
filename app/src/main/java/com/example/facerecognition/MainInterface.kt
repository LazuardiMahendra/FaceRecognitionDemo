package com.example.facerecognition

import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.view.LifecycleCameraController
import com.example.facerecognition.databinding.ActivityMainBinding
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.InputStream
import java.util.concurrent.ExecutorService

interface MainActivityInterface {
    var binding: ActivityMainBinding
    var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    fun initListener()
    fun dialogAlert()

    fun showSnackBar(error: String)
    fun startGetLoading()
    fun stopGetLoading()
}

interface MainPresenterInterface {
    var currentPhase: Int
    var phaseStartTime: Long
    var challengePassed: Boolean
    var isSmile: Boolean
    var isLeftEyeClosed: Boolean
    var isRightEyeClosed: Boolean
    var challengeCurrent: String
    var challengeList: MutableList<String>
    var hasCapturePhoto: Boolean
    var currentPhotoFile: File?
    var cameraExecutor: ExecutorService
    var faceUri1: Uri
    var faceUri2: Uri
    var faceResult1: String
    var faceResult2: String

    fun startCamera()
    fun resetPhases()
    fun showSnackBar(error: String)
    fun stopGetLoading()
    fun generateChallenges(): String
    fun parseDetectFace1(obj: JSONArray)
    fun parseDetectFace2(obj: JSONArray)
    fun parseDownloadImage(inputStream: InputStream)
    fun parseVerifyFace(obj: JSONObject)

    fun requiredVerification(face: Face, cameraController: LifecycleCameraController)
    fun capturePhoto(cameraController: LifecycleCameraController)
    fun createImageFile(): File
    fun checkingChallengeVerification(face: Face)
    fun challengeVerification(face: Face, cameraController: LifecycleCameraController)
    fun setupPermission(handler: String)
    fun launchPermission(handler: String)
    fun handleRequestPermissionLauncher(result: Map<String, Boolean>)
    fun getDownloadImage()
    fun getVerifyFace()
}

interface MainInteractorInterface {
    fun showSnackBar(error: String)
    fun stopGetLoading()

    fun parseDetectFace1(obj: JSONArray)
    fun parseDetectFace2(obj: JSONArray)
    fun parseDownloadImage(inputStream: InputStream)
    fun parseVerifyFace(obj: JSONObject)

    fun detectFace1(face1: Uri)
    fun detectFace2(byteArray: ByteArray)
    fun downloadImage(linkImage: String)
    fun verifyFace(faceId1: String, faceId2: String)
}

interface MainNetworkInterface {
    fun detectFace1(face1: Uri)
    fun downloadImage(linkImage: String)
    fun detectFace2(dataByteArray: ByteArray)
    fun verifyFace(faceId1: String, faceId2: String)
}
