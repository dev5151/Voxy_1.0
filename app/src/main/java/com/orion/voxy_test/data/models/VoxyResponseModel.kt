package com.orion.voxy_test.data.models

import com.google.gson.annotations.SerializedName

data class VoxyResponseModel(
    @SerializedName("arguments") val arguments: Arguments? = null,
    @SerializedName("screenName") val screenName: String? = null
)