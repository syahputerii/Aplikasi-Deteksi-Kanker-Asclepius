package com.dicoding.asclepius.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dicoding.asclepius.databinding.ActivityResultBinding
import android.net.Uri
import android.widget.Toast
import com.dicoding.asclepius.data.local.room.PredictionHistoryDatabase
import com.dicoding.asclepius.helper.ImageClassifierHelper
import org.tensorflow.lite.task.vision.classifier.Classifications
import com.dicoding.asclepius.data.local.entity.PredictionHistoryEntity
import java.text.NumberFormat
import java.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.activity.viewModels
import androidx.lifecycle.Observer

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var imageClassifier: ImageClassifierHelper
    private lateinit var db: PredictionHistoryDatabase
    private val imageViewModel: ImageViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = PredictionHistoryDatabase.getInstance(this)

        supportActionBar?.apply { hide() }

        binding.backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.viewHistoryButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        imageViewModel.imageUri.observe(this, Observer { uri ->
            uri?.let {
                displayImage(it)
                initializeClassifier(it)
                imageClassifier.classifyStaticImage(it)
            }
        })

        if (imageViewModel.imageUri.value == null) {
            intent.getStringExtra(EXTRA_IMAGE_URI)?.let { uriString ->
                val imageUri = Uri.parse(uriString)
                imageViewModel.setImageUri(imageUri)
            }
        }
    }

    private fun initializeClassifier(imageUri: Uri) {
        imageClassifier = ImageClassifierHelper(
            context = this,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    Toast.makeText(this@ResultActivity, error, Toast.LENGTH_SHORT).show()
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    results?.takeIf { it.isNotEmpty() }?.let { classificationResults ->
                        val topCategory = classificationResults[0].categories.maxByOrNull { it.score }
                        runOnUiThread {
                            topCategory?.let {
                                updateResultView(imageUri, it.label, it.score)
                                savePredictionToDatabase(imageUri.toString(), it.label, it.score)
                            } ?: clearResultView()
                        }
                    }
                }
            }
        )
    }

    private fun displayImage(uri: Uri) {
        binding.resultImage.setImageURI(uri)
    }

    private fun updateResultView(imageUri: Uri, label: String, score: Float) {
        binding.resultImage.setImageURI(imageUri)
        binding.resultText.text = "${NumberFormat.getPercentInstance().format(score)} $label"
    }

    private fun clearResultView() {
        binding.resultText.text = ""
    }

    private fun savePredictionToDatabase(imagePath: String, label: String, confidence: Float) {
        val predictionHistory = PredictionHistoryEntity(
            imagePath = imagePath,
            predictionResult = label,
            confidenceScore = confidence,
            timestamp = Calendar.getInstance().timeInMillis
        )

        CoroutineScope(Dispatchers.IO).launch {
            db.predictionHistoryDao().insertPrediction(predictionHistory)
        }
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image_uri"
    }
}