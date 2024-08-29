package com.orion.voxy_test.data.api

class VoxyApiHelper(private val voxyAPI: IVoxyApi) {
    suspend fun getVoxyResponse(prompt:String) = voxyAPI.getVoxyResponse(prompt)
}