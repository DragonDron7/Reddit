package com.dronios777.myreddit.data.model.friends

import com.google.gson.annotations.SerializedName


data class Friend(
    @SerializedName("date") var date: Int? = null,
    @SerializedName("rel_id") var relId: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("id") var id: String? = null
)