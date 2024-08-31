package com.orion.voxy_test.data.models

import com.google.gson.annotations.SerializedName

data class Arguments(
    @SerializedName("option") val option: String? = null,
    @SerializedName("preference") val preference: String? = null
)