package com.dronios777.myreddit.data.model.me

import com.google.gson.annotations.SerializedName

data class MeResponse(
    @SerializedName("name") var name: String? = null,
    @SerializedName("icon_img") var iconImg: String? = null,
    @SerializedName("subreddit") var more_infos: UserDataSub? = null,
    @SerializedName("awarder_karma") var awarder_karma: Int? = 0,
    @SerializedName("awardee_karma") var awardee_karma: Int? = 0,
    @SerializedName("link_karma") var publication_karma: Int? = 0,
    @SerializedName("comment_karma") var comment_karma: Int? = 0,
    @SerializedName("total_karma") var total_karma: Int? = 0,
    @SerializedName("created_utc") var account_creation_date: Long? = 0,
    @SerializedName("num_friends") var numFriends: Int? = 0,
) {
    data class UserDataSub(
        @SerializedName("subscribers") var subscribers: Int? = null,
        @SerializedName("public_description") var description: String? = null
    )
}