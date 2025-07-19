package com.example.facerecognition

import android.content.Intent
import android.media.FaceDetector
import androidx.activity.result.ActivityResultLauncher
import androidx.camera.view.LifecycleCameraController
import com.example.facerecognition.databinding.ActivityMainBinding
import com.google.mlkit.vision.face.Face
import java.io.File
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
    fun requiredVerification(face: Face, cameraController: LifecycleCameraController)
    fun generateChallenges(): String
    fun capturePhoto(cameraController: LifecycleCameraController)
    fun createImageFile(): File
    var currentPhotoFile: File?
    fun checkingChallengeVerification(face: Face)
    fun challengeVerification(face: Face)
    fun savedToGallery(photoFile: File)
    var cameraExecutor: ExecutorService
    fun setupPermission(handler: String)
    fun launchPermission(handler: String)
    fun handleRequestPermissionLauncher(result: Map<String, Boolean>)
}

interface MainInteractorInterface {
}

interface MainNetworkInterface {
}
