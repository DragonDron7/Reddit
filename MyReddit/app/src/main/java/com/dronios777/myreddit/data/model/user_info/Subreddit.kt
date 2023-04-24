package com.dronios777.myreddit.data.model.user_info

import com.google.gson.annotations.SerializedName


data class Subreddit(

    @SerializedName("default_set") var defaultSet: Boolean? = null,
    @SerializedName("user_is_contributor") var userIsContributor: Boolean? = null,
    @SerializedName("banner_img") var bannerImg: String? = null,
    @SerializedName("allowed_media_in_comments") var allowedMediaInComments: ArrayList<String> = arrayListOf(),
    @SerializedName("user_is_banned") var userIsBanned: Boolean? = null,
    @SerializedName("free_form_reports") var freeFormReports: Boolean? = null,
    @SerializedName("community_icon") var communityIcon: String? = null,
    @SerializedName("show_media") var showMedia: Boolean? = null,
    @SerializedName("icon_color") var iconColor: String? = null,
    @SerializedName("user_is_muted") var userIsMuted: String? = null,
    @SerializedName("display_name") var displayName: String? = null,
    @SerializedName("header_img") var headerImg: String? = null,
    @SerializedName("title") var title: String? = null,
    @SerializedName("coins") var coins: Int? = null,
    @SerializedName("previous_names") var previousNames: ArrayList<String> = arrayListOf(),
    @SerializedName("over_18") var over18: Boolean? = null,
    @SerializedName("icon_size") var iconSize: ArrayList<Int> = arrayListOf(),
    @SerializedName("primary_color") var primaryColor: String? = null,
    @SerializedName("icon_img") var iconImg: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("submit_link_label") var submitLinkLabel: String? = null,
    @SerializedName("header_size") var headerSize: String? = null,
    @SerializedName("restrict_posting") var restrictPosting: Boolean? = null,
    @SerializedName("restrict_commenting") var restrictCommenting: Boolean? = null,
    @SerializedName("subscribers") var subscribers: Int? = null,
    @SerializedName("submit_text_label") var submitTextLabel: String? = null,
    @SerializedName("is_default_icon") var isDefaultIcon: Boolean? = null,
    @SerializedName("link_flair_position") var linkFlairPosition: String? = null,
    @SerializedName("display_name_prefixed") var displayNamePrefixed: String? = null,
    @SerializedName("key_color") var keyColor: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("is_default_banner") var isDefaultBanner: Boolean? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("quarantine") var quarantine: Boolean? = null,
    @SerializedName("banner_size") var bannerSize: String? = null,
    @SerializedName("user_is_moderator") var userIsModerator: Boolean? = null,
    @SerializedName("accept_followers") var acceptFollowers: Boolean? = null,
    @SerializedName("public_description") var publicDescription: String? = null,
    @SerializedName("link_flair_enabled") var linkFlairEnabled: Boolean? = null,
    @SerializedName("disable_contributor_requests") var disableContributorRequests: Boolean? = null,
    @SerializedName("subreddit_type") var subredditType: String? = null,
    @SerializedName("user_is_subscriber") var userIsSubscriber: Boolean? = null

)