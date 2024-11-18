package com.dicoding.asclepius.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.response.ArticlesItem
import com.dicoding.asclepius.data.retrofit.ApiConfig
import com.dicoding.asclepius.data.response.ArticleResponse
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _articles = MutableLiveData<List<ArticlesItem>>()
    val articles: LiveData<List<ArticlesItem>>
        get() = _articles

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _isLoading.postValue(false)
        _errorMessage.postValue(throwable.message ?: "Unknown error occurred")
    }

    fun fetchNewsData(apiKey: String) {
        _isLoading.postValue(true)

        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            val apiService = ApiConfig.getApiService()
            apiService.fetchHealthNews(apiKey = apiKey).enqueue(object : Callback<ArticleResponse> {
                override fun onResponse(
                    call: Call<ArticleResponse>,
                    response: Response<ArticleResponse>
                ) {
                    _isLoading.postValue(false)
                    if (response.isSuccessful) {
                        val validArticles =
                            response.body()?.articles?.filterNotNull()?.filter { article ->
                                article.title != "[Removed]" && article.description != "[Removed]"
                            } ?: emptyList()
                        _articles.postValue(validArticles)
                    } else {
                        _errorMessage.postValue("Error code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ArticleResponse>, error: Throwable) {
                    _isLoading.postValue(false)
                    _errorMessage.postValue(
                        error.localizedMessage ?: "An unexpected issue occurred"
                    )
                }
            })
        }
    }
}