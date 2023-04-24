package com.dronios777.myreddit.data.model.token

import com.google.gson.annotations.SerializedName

data class ResponseToken(
    @SerializedName("access_token") val access_token: String? = null,// токен
    @SerializedName("token_type") val token_type: String? = null,
    @SerializedName("expires_in") val expires_in: String? = null,
    @SerializedName("scope") val scope: String? = null,
    @SerializedName("refresh_token") val refresh_token: String? = null
)