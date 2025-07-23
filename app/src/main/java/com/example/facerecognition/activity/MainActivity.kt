package com.example.facerecognition.activity


import android.app.Dialog
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.facerecognition.MainActivityInterface
import com.example.facerecognition.MainPresenter
import com.example.facerecognition.databinding.ActivityMainBinding
import com.example.facerecognition.databinding.DialogAlertBinding
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity(), MainActivityInterface {

    private var presenter: MainPresenter? = null

    override lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    override lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListener()

        presenter = MainPresenter(this, this)
        presenter?.setupPermission("default")

        presenter?.cameraExecutor = Executors.newSingleThreadExecutor()
    }

    override fun initListener() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                presenter?.handleRequestPermissionLauncher(result)
            }
    }

    override fun dialogAlert() {
        val dialog = Dialog(this)
        val dialogBinding = DialogAlertBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.okButton.setOnClickListener {
            presenter?.resetPhases()
            presenter?.startCamera()
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.cameraExecutor!!.shutdown()
    }


}