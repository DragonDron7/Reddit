package com.dronios777.myreddit.data.model.friends

import com.google.gson.annotations.SerializedName


data class FriendsResponse(
    @SerializedName("kind") var kind: String? = null,
    @SerializedName("data") var data: Data? = Data()
)