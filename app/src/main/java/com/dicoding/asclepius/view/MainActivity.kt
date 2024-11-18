package com.dicoding.asclepius.view

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.yalantis.ucrop.UCrop
import java.io.File
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val imageViewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageViewModel.imageUri.observe(this, Observer { uri ->
            refreshButtonStatus()
            uri?.let {
                showImage(it)
            }
        })

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }

        refreshButtonStatus()
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery =
        registerForActivityResult(
            ActivityResultContracts.PickVisualMedia()
        ){ uri: Uri? ->
            if (uri != null) {
                moveToCropScreen(uri)
            } else {
                Log.d("Photo Picker", "No media selected")
            }
        }

    private fun showImage(uri: Uri) {
        Log.d("Image URI", "showImage: $uri")
        binding.previewImageView.setImageURI(uri)
        refreshButtonStatus()
    }

    private fun analyzeImage() {
        imageViewModel.imageUri.value?.let { uri ->
            val analysisIntent = Intent(this, ResultActivity::class.java).apply {
                putExtra(ResultActivity.EXTRA_IMAGE_URI, uri.toString())
            }
            startActivity(analysisIntent)
        } ?: showToast("No image selected")
    }

    private fun moveToResult() {
        val intent = Intent(this, ResultActivity::class.java)
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private val cropImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                val croppedUri = data?.let { UCrop.getOutput(it) }
                croppedUri?.let {
                    imageViewModel.setImageUri(it)
                    showImage(it)
                } ?: Log.e(TAG, "Failed to retrieve cropped image URI")
            } else if (result.resultCode == UCrop.RESULT_ERROR) {
                val error = UCrop.getError(result.data!!)
                showToast(error?.message ?: "Unknown error occurred")
                Log.e(TAG, "Error during cropping: ${error?.message}")
            }
        }

    private fun moveToCropScreen(imageUri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "${System.currentTimeMillis()}.jpg"))
        val cropIntent = UCrop.of(imageUri, destinationUri).getIntent(this)
        cropImageLauncher.launch(cropIntent)
    }

    private fun refreshButtonStatus() {
        binding.analyzeButton.isEnabled = imageViewModel.imageUri.value != null
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}