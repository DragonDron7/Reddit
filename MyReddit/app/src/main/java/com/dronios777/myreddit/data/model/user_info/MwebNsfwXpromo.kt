package com.dronios777.myreddit.data.model.user_info

import com.google.gson.annotations.SerializedName


data class MwebNsfwXpromo(

    @SerializedName("owner") var owner: String? = null,
    @SerializedName("variant") var variant: String? = null,
    @SerializedName("experiment_id") var experimentId: Int? = null

)