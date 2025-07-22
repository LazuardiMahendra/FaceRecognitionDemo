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
}

interface MainPresenterInterface {
    fun startCamera()
    fun resetPhases()
    var currentPhase: Int
    var phaseStartTime: Long
    var challengePassed: Boolean
    var isSmile: Boolean
    var isLeftEyeClosed: Boolean
    var isRightEyeClosed: Boolean
    var challengeCurrent: String
    var challengeList: MutableList<String>
    var hasCapturePhoto: Boolean
    var hasSavedToGallery: Boolean
    fun generateChallenges(): String
    var currentPhotoFile: File?
    var cameraExecutor: ExecutorService

    fun parseDetectFace1(obj: JSONArray)
    fun parseDetectFace2(obj: JSONArray)
    fun parseDownloadImage(inputStream: InputStream)
    fun parseVerifyFace(obj: JSONObject)


    fun requiredVerification(face: Face, cameraController: LifecycleCameraController)
    fun capturePhoto(cameraController: LifecycleCameraController)
    fun createImageFile(): File
    fun checkingChallengeVerification(face: Face)
    fun challengeVerification(face: Face)
    fun savedToGallery(photoFile: File)
    fun setupPermission(handler: String)
    fun launchPermission(handler: String)
    fun handleRequestPermissionLauncher(result: Map<String, Boolean>)
    var faceUri1: Uri
    var faceUri2: Uri


    var faceResult1: String
    var faceResult2: String
    fun getDownloadImage()
    fun getVerifyFace()
}

interface MainInteractorInterface {
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
