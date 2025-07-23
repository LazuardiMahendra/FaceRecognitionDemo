package com.example.facerecognition

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageSavedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.collection.arraySetOf
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.facerecognition.activity.CompleteActivity
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceLandmark
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainPresenter(_view: MainActivityInterface, val context: Context) : MainPresenterInterface {

    private var view: MainActivityInterface = _view
    private var model: MainInteractor? = null

    override var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    private lateinit var faceDetector: FaceDetector

    override lateinit var faceUri1: Uri
    override lateinit var faceUri2: Uri

    override var faceResult1: String = ""
    override var faceResult2: String = ""

    override var currentPhase = 1
    override var phaseStartTime: Long = 0L
    override var challengePassed = false

    override var isSmile = false
    override var isLeftEyeClosed = false
    override var isRightEyeClosed = false

    override var challengeCurrent = ""
    override var challengeList: MutableList<String> = mutableListOf()

    override var currentPhotoFile: File? = null

    override var hasCapturePhoto = true

    init {
        model = MainInteractor(this, context)

    }

    override fun setupPermission(handler: String) {
        val permissionCamera =
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)

        val permissionReadStorage =
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionWriteStorage =
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (permissionReadStorage == PackageManager.PERMISSION_GRANTED && permissionWriteStorage == PackageManager.PERMISSION_GRANTED && permissionCamera == PackageManager.PERMISSION_GRANTED) {
                when (handler) {
                    "default" -> startCamera()
                }
            } else {
                launchPermission(handler)
            }
        } else {
            if (permissionCamera == PackageManager.PERMISSION_GRANTED) {
                when (handler) {
                    "default" -> startCamera()
                }
            } else {
                launchPermission(handler)
            }
        }
    }

    override fun launchPermission(handler: String) {
        val permissionList = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
        )

        when (handler) {
            "default" -> view.requestPermissionLauncher.launch(permissionList)
        }
    }

    override fun handleRequestPermissionLauncher(result: Map<String, Boolean>) {
        var successCount = 0
        result.entries.forEach {
            if (it.value) {
                successCount += 1
            }
        }
        if (successCount == (if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) 3 else 1)) {
            startCamera()
        }
    }

    override fun getDownloadImage() {
        val url =
            "https://www.simpleimageresizer.com/_uploads/photos/811d21f2/WhatsApp_Image_2025-07-05_at_13.34.25_50.jpeg"
        model?.downloadImage(url)
    }

    override fun getVerifyFace() {
        model?.verifyFace(faceResult1, faceResult2)
    }

    override fun parseDetectFace1(obj: JSONArray) {
        Log.d("Parse", "face 1: $obj")

        val jsonObj = obj.getJSONObject(0)
        faceResult1 = jsonObj.getString("faceId")

        getDownloadImage()
    }

    override fun parseDownloadImage(inputStream: InputStream) {
        model?.detectFace2(inputStreamToByteArray(inputStream))
    }

    private fun inputStreamToByteArray(inputStream: InputStream): ByteArray {
        val outputStream = ByteArrayOutputStream()
        inputStream.use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
        return outputStream.toByteArray()
    }

    override fun parseDetectFace2(obj: JSONArray) {
        Log.d("Parse", "face 2: $obj")

        val jsonObj = obj.getJSONObject(0)
        faceResult2 = jsonObj.getString("faceId")

        getVerifyFace()
    }

    override fun parseVerifyFace(obj: JSONObject) {
        val isIdentical = obj.getBoolean("isIdentical")
        Log.d("Parse", "data verif: $obj")

        if (isIdentical) {
            val intent = Intent(context, CompleteActivity::class.java)
            context.startActivity(intent)
        } else {
            view.dialogAlert()
        }
    }

    override fun startCamera() {
        val cameraController = LifecycleCameraController(context)
        val previewView: PreviewView = view.binding.previewView

        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        cameraController.bindToLifecycle(context as LifecycleOwner)

        previewView.controller = cameraController
        previewView.setOnTouchListener { _, _ -> true }

        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL).build()

        faceDetector = FaceDetection.getClient(options)

        cameraController.setImageAnalysisAnalyzer(
            ContextCompat.getMainExecutor(context), MlKitAnalyzer(
                listOf(faceDetector),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                ContextCompat.getMainExecutor(context)
            ) { result: MlKitAnalyzer.Result? ->
                val faces = result?.getValue(faceDetector)

                if (faces.isNullOrEmpty()) {
                    resetPhases()
                    view.binding.statusText.text = "No face detected ❌"
                    return@MlKitAnalyzer
                }

                val face = faces[0]
                when (currentPhase) {
                    1 -> requiredVerification(face, cameraController)
                    2 -> challengeVerification(face, cameraController)
                }

            })

    }

    override fun resetPhases() {
        currentPhase = 1
        phaseStartTime = 0L
        challengeCurrent = ""

        challengePassed = false

        isSmile = false
        isLeftEyeClosed = false
        isRightEyeClosed = false

        hasCapturePhoto = true
        challengeList.clear()
    }

    override fun requiredVerification(face: Face, cameraController: LifecycleCameraController) {
        val rotX = face.headEulerAngleX
        val rotY = face.headEulerAngleY
        val rotZ = face.headEulerAngleZ

        val facingApprove = (rotX in -10f..10f && rotY in -15f..15f && rotZ in -10f..10f)
        val hasContours = face.allContours.isNotEmpty()
        val hasLandmarks = listOf(
            FaceLandmark.LEFT_EAR,
            FaceLandmark.RIGHT_EAR,
            FaceLandmark.RIGHT_EYE,
            FaceLandmark.LEFT_EYE,
            FaceLandmark.NOSE_BASE,
            FaceLandmark.MOUTH_BOTTOM,
            FaceLandmark.MOUTH_LEFT,
            FaceLandmark.MOUTH_RIGHT
        ).all { face.getLandmark(it) != null }

        val isValid = facingApprove && hasContours && hasLandmarks

        if (phaseStartTime == 0L) phaseStartTime = System.currentTimeMillis()
        val elapsed = System.currentTimeMillis() - phaseStartTime
        view.binding.statusText.text = "Hold your head position"
        view.binding.timeTxt.text = "${(2000 - elapsed).coerceAtLeast(0) / 2000}s"

        if (elapsed >= 1000) {
            if (isValid) {
                if ((face.smilingProbability ?: 0F) > 0.7) isSmile = true
                if ((face.leftEyeOpenProbability ?: 0F) < 0.2) isLeftEyeClosed = true
                if ((face.rightEyeOpenProbability ?: 0F) < 0.2) isRightEyeClosed = true

                currentPhase = 2
                phaseStartTime = 0L
                challengePassed = false
                challengeCurrent = generateChallenges()
                capturePhoto(cameraController)

            } else {
                resetPhases()
                view.binding.statusText.text = "Invalid face ❌"
            }
        }
    }

    override fun challengeVerification(face: Face, cameraController: LifecycleCameraController) {
        val rotX = face.headEulerAngleX
        val rotY = face.headEulerAngleY
        val rotZ = face.headEulerAngleZ

        val facingApprove = (rotX in -10f..10f && rotY in -15f..15f && rotZ in -10f..10f)
        val hasContours = face.allContours.isNotEmpty()
        val hasLandmarks = listOf(
            FaceLandmark.LEFT_EAR,
            FaceLandmark.RIGHT_EAR,
            FaceLandmark.RIGHT_EYE,
            FaceLandmark.LEFT_EYE,
            FaceLandmark.NOSE_BASE,
            FaceLandmark.MOUTH_BOTTOM,
            FaceLandmark.MOUTH_LEFT,
            FaceLandmark.MOUTH_RIGHT
        ).all { face.getLandmark(it) != null }

        val isValid = facingApprove && hasContours && hasLandmarks

        if (!isValid) resetPhases()

        view.binding.statusText.text = "Please $challengeCurrent"
        view.binding.timeTxt.visibility = View.GONE

        checkingChallengeVerification(face)
        if (challengePassed && currentPhotoFile != null) {
            model?.detectFace1(Uri.fromFile(currentPhotoFile))
            cameraController.unbind()
        }
    }

    override fun generateChallenges(): String {
        if (!isSmile) challengeList.add("smile")
        if (!isLeftEyeClosed) challengeList.add("closed left eye")
        if (!isRightEyeClosed) challengeList.add("closed right eye")
        if (!isRightEyeClosed && !isLeftEyeClosed) challengeList.add("blink")


        return challengeList.random()
    }

    override fun checkingChallengeVerification(face: Face) {
        when (challengeCurrent) {
            "smile" -> {
                if ((face.smilingProbability ?: 0F) > 0.7) {
                    challengePassed = true
                }
            }

            "closed left eye" -> {
                if ((face.leftEyeOpenProbability ?: 0F) > 0.2 && (face.rightEyeOpenProbability
                        ?: 0F) < 0.4
                ) {
                    challengePassed = true
                }
            }

            "closed right eye" -> {
                if ((face.rightEyeOpenProbability ?: 0F) > 0.2 && (face.leftEyeOpenProbability
                        ?: 0F) < 0.4
                ) {
                    challengePassed = true
                }
            }

            "blink" -> {
                if ((face.rightEyeOpenProbability ?: 0F) < 0.2 && (face.leftEyeOpenProbability
                        ?: 0F) < 0.2
                ) {
                    challengePassed = true
                }
            }

            else -> {
                resetPhases()
            }
        }
    }

    override fun capturePhoto(cameraController: LifecycleCameraController) {
        if (!hasCapturePhoto) return

        val previewView: PreviewView = view.binding.previewView

        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        cameraController.bindToLifecycle(context as LifecycleOwner)
        previewView.controller = cameraController

        val outputFile = createImageFile()
        val outputOption = ImageCapture.OutputFileOptions.Builder(outputFile).build()
        val executor = ContextCompat.getMainExecutor(context)

        if (hasCapturePhoto) {
            cameraController.takePicture(outputOption, executor, object : OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    currentPhotoFile = outputFile
                    hasCapturePhoto = false
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e("RESULT", "Error taking photo: ${exception.message}")
                }
            })
        }
    }

    override fun createImageFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyMMdd_HHmmss_SSS", Locale.getDefault()).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }


}