package com.dicoding.asclepius.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityHomeBinding
    private lateinit var adapter: ArticleAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        homeViewModel.fetchNewsData(API_KEY)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupListeners() {
        binding.cekCancer.setOnClickListener {
            navigateToMainActivity()
        }
    }

    private fun observeViewModel() {
        homeViewModel.articles.observe(this, Observer { articles ->
            adapter = ArticleAdapter(articles)
            binding.recyclerView.adapter = adapter
        })

        homeViewModel.isLoading.observe(this, Observer { isLoading ->
            toggleLoading(isLoading)
        })

        homeViewModel.errorMessage.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                showToast(it)
            }
        })
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val API_KEY = "1af49d7815f246a683a97c0d2fcdd480"
    }
}