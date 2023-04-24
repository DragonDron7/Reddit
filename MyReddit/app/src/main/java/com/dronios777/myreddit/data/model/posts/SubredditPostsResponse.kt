package com.dronios777.myreddit.data.model.posts

import com.google.gson.annotations.SerializedName

data class SubredditPostsResponse(
    @SerializedName("data") val data: Data? = null
) {
    data class Data(
        @SerializedName("after") val after: String? = null,
        @SerializedName("dist") val size: String? = null,
        @SerializedName("modhash") val modhash: String? = null,
        @SerializedName("geo_filter") val geo_filter: String? = null,
        @SerializedName("children") val posts: List<Posts>? = null
    ) {
        data class Posts(
            @SerializedName("data") val postData: PostData? = null
        ) {
            data class PostData(
                @SerializedName("subreddit_name_prefixed") val subNamePref: String? = null,
                @SerializedName("approved_at_utc") val kind: String? = null,
                @SerializedName("author") val author: String? = null,
                @SerializedName("created") val created: Double? = null,
                @SerializedName("over_18") val over18: Boolean? = null,
                @SerializedName("subreddit") val subreddit: String? = null,
                @SerializedName("subreddit_id") val subredditId: String? = null,
                @SerializedName("selftext") val description: String? = null,
                @SerializedName("title") val title: String? = null,
                @SerializedName("post_hint") val post_hint: String? = null,
                @SerializedName("domain") val domain: String? = null,
                @SerializedName("is_video") val is_video: Boolean? = null,
                @SerializedName("url_overridden_by_dest") val simple_medial_url: String? = null,
                @SerializedName("secure_media") val secure_media: SecureMedia? = null,
                @SerializedName("id") val id: String?,
                @SerializedName("num_comments") val numComments: String? = "0",
                @SerializedName("ups") val ups: String? = "0",
                @SerializedName("name") val name: String? = null,
                @SerializedName("permalink") val permalink: String? = null,
                @SerializedName("likes") val likes: Boolean? = null,

            ) {
                data class SecureMedia(
                    @SerializedName("reddit_video") val reddit_video: RedditVideo? = null,
                    @SerializedName("oembed") val oembed: Oembed? = null
                ) {
                    data class RedditVideo(
                        @SerializedName("fallback_url") val complex_media_url: String? = null,
                        @SerializedName("hls_url") val hls_url: String? = null
                    )

                    data class Oembed(
                        @SerializedName("author_url") val author_url: String? = null,
                        @SerializedName("thumbnail_url") val thumbnail_url: String? = null
                    )
                }
            }
        }
    }
}

