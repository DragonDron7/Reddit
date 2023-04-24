package com.dronios777.myreddit.data.model.friends

import com.google.gson.annotations.SerializedName


data class Data(
    @SerializedName("children") var friends: ArrayList<Friend> = arrayListOf()
)