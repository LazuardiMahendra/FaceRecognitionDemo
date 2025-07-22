package com.example.facerecognition


import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.facerecognition.databinding.ActivityMainBinding
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

        presenter?.getDownloadImage()
    }

    override fun initListener() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
                presenter?.handleRequestPermissionLauncher(result)
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.cameraExecutor!!.shutdown()
    }


}