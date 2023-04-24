package com.dronios777.myreddit.data.model.user_info

import com.google.gson.annotations.SerializedName

data class UserInfoResponse(
    @SerializedName("kind") var kind: String? = null,
    @SerializedName("data") var data: UserInfo? = UserInfo()

)