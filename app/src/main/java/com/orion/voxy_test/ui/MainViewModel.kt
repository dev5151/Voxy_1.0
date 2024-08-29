package com.orion.voxy_test.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orion.voxy_test.data.models.VoxyResponseModel
import com.orion.voxy_test.data.repo.MainRepository
import com.orion.voxy_test.data.utils.ApiResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val _data: MutableLiveData<ApiResponse<VoxyResponseModel?>> = MutableLiveData()
    val data: MutableLiveData<ApiResponse<VoxyResponseModel?>>
        get() = _data

    fun getVoxyRes(prompt: String) = viewModelScope.launch(Dispatchers.IO) {
        _data.postValue(ApiResponse.Loading)
        _data.postValue(mainRepository.getVoxyRes(prompt))
    }
}