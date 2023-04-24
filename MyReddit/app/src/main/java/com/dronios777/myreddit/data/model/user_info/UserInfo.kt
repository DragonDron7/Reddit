package com.dronios777.myreddit.data.model.user_info

import com.dronios777.myreddit.data.model.me.MeResponse
import com.google.gson.annotations.SerializedName


data class UserInfo(

    @SerializedName("name") var name: String? = null,
    @SerializedName("icon_img") var iconImg: String? = null,
    @SerializedName("subreddit") var more_infos: MeResponse.UserDataSub? = null,
    @SerializedName("awarder_karma") var awarder_karma: Int? = 0,
    @SerializedName("awardee_karma") var awardee_karma: Int? = 0,
    @SerializedName("link_karma") var publication_karma: Int? = 0,
    @SerializedName("comment_karma") var comment_karma: Int? = 0,
    @SerializedName("total_karma") var total_karma: Int? = 0,
    @SerializedName("created_utc") var account_creation_date: Long? = 0,
    @SerializedName("snoovatar_img") var snoovatarImg: String? = "",
    )