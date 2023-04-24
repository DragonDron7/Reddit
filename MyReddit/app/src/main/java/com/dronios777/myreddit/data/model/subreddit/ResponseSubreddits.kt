package com.dronios777.myreddit.data.model.subreddit

import com.google.gson.annotations.SerializedName

data class ResponseSubreddits(@SerializedName("data") val `data`: Data? = null) {
    data class Data(
        @SerializedName("after") val after: String? = null,
        @SerializedName("before") val before: Any? = null,
        @SerializedName("children") val children: List<Children>? = null,
        @SerializedName("dist") val dist: Int? = null,
        @SerializedName("geo_filter") val geoFilter: String? = null,
        @SerializedName("modhash") val modhash: Any? = null
    ) {
        data class Children(
            @SerializedName("data") val `data`: Data? = null
        ) {
            data class Data(
                @SerializedName("banner_background_image") val bannerBackgroundImage: String? = "",
                @SerializedName("banner_img") val bannerImg: String? = "",
                @SerializedName("community_icon") val communityIcon: String? = "",
                @SerializedName("created") val created: Double? = 0.0,
                @SerializedName("created_utc") val createdUtc: Double? = 0.0,
                @SerializedName("description") val description: String? = "",
                @SerializedName("display_name") val displayName: String? = "",
                @SerializedName("display_name_prefixed") val displayNamePrefixed: String? = "",
                @SerializedName("header_img") val headerImg: Any? = "",
                @SerializedName("header_size") val headerSize: Any? = "",
                @SerializedName("header_title") val headerTitle: String? = "",
                @SerializedName("icon_img") val iconImg: String? = "",
                @SerializedName("id") val id: String?,
                @SerializedName("mobile_banner_image") val mobileBannerImage: String? = null,
                @SerializedName("name") val name: String? = null,
                @SerializedName("over18") val over18: Boolean? = null,
                @SerializedName("public_description") val publicDescription: String? = null,
                @SerializedName("subscribers") val subscribers: Int? = null,
                @SerializedName("title") val title: String? = null,
                @SerializedName("user_is_subscriber") var userIsSubscriber: Boolean? = false
            )
        }
    }
}