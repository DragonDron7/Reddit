package com.dronios777.myreddit.data.model.posts

import com.google.gson.annotations.SerializedName

data class CommentsResponse(
    @SerializedName("data") val data: Data? = null
) {
    data class Data(
        @SerializedName("after") val after: String? = null,
        @SerializedName("before") val before: String? = null,
        @SerializedName("dist") val size: String? = null,
        @SerializedName("modhash") val modhash: String? = null,
        @SerializedName("geo_filter") val geo_filter: String? = null,
        @SerializedName("children") val comments: ArrayList<Comments> = arrayListOf(),
    ) {
        data class Comments(
            @SerializedName("data") val commentsData: CommentsData? = null
        ) {
            data class CommentsData(

                @SerializedName("author") val author: String? = null,
                @SerializedName("body") val body: String? = null,
                @SerializedName("created_utc") val created_utc: Double? = null,
                @SerializedName("id") val id: String?,
                @SerializedName("link_id") val link_id: String? = null,
                @SerializedName("name") val name: String? = null,
                @SerializedName("saved") val saved: Boolean? = null,
                @SerializedName("selftext") val selftext: String? = null,
                @SerializedName("title") val title: String? = null,
                @SerializedName("url") val url: String? = null,
                @SerializedName("author_fullname") val author_fullname: String? = null,
                @SerializedName("ups") val ups: String? = "0",
                @SerializedName("permalink") val permalink: String? = null,
                @SerializedName("likes") var likes: Boolean? = null,
                @SerializedName("subreddit_name_prefixed") var subreddit_name_prefixed: String? = null,
            )
        }
    }
}


