package com.orion.voxy_test.data.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.orion.voxy_test.data.api.VoxyApiHelper
import com.orion.voxy_test.data.repo.MainRepository
import com.orion.voxy_test.ui.MainViewModel

class ViewModelFactory(private val voxyAPIHelper: VoxyApiHelper) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(voxyAPIHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}