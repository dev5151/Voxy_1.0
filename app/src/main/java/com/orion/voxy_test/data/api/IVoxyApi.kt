package com.orion.voxy_test.data.api

import com.orion.voxy_test.data.models.VoxyResponseModel
import retrofit2.Response
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface IVoxyApi {

    @Headers("x-api-key: AIzaSyBBQ11aXH5o8cBI3Gn6Fp1nIrt7ELrVACU")
    @POST("/chatbot")
    suspend fun getVoxyResponse(@Query("user_prompt") userPrompt: String): Response<VoxyResponseModel>
}