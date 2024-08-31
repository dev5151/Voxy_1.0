package com.orion.voxy_test.data.repo

import android.util.Log
import com.orion.voxy_test.data.api.VoxyApiHelper
import com.orion.voxy_test.data.models.VoxyResponseModel
import com.orion.voxy_test.data.utils.ApiResponse

class MainRepository(private val voxyApiHelper: VoxyApiHelper) {

    suspend fun getVoxyRes(prompt: String): ApiResponse<VoxyResponseModel?> {
        return try {
            val response = voxyApiHelper.getVoxyResponse(prompt)
            Log.d("speech", "Res -> " + response.toString())
            if (response.isSuccessful) {
                Log.d("speech", "Res Body -> " + response.body().toString())
                ApiResponse.Success(response.body())
            } else {
                ApiResponse.Error(Throwable("Error: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            // Handle exceptions like network failure
            ApiResponse.Error(Throwable(e.message.toString()))
        }
    }
}