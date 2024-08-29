package com.orion.voxy_test.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {
    private const val BASE_URL = "https://chatbot-response-2-8l5560u1.an.gateway.dev"

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build() //Doesn't require the adapter
    }

    val voxyAPI: IVoxyApi = getRetrofit().create(IVoxyApi::class.java)
}