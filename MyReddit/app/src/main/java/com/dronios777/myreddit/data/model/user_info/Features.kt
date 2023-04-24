package com.dronios777.myreddit.data.model.user_info

import com.google.gson.annotations.SerializedName

data class Features(

    @SerializedName("mod_service_mute_writes") var modServiceMuteWrites: Boolean? = null,
    @SerializedName("promoted_trend_blanks") var promotedTrendBlanks: Boolean? = null,
    @SerializedName("show_amp_link") var showAmpLink: Boolean? = null,
    @SerializedName("is_email_permission_required") var isEmailPermissionRequired: Boolean? = null,
    @SerializedName("mod_awards") var modAwards: Boolean? = null,
    @SerializedName("expensive_coins_package") var expensiveCoinsPackage: Boolean? = null,
    @SerializedName("chat_subreddit") var chatSubreddit: Boolean? = null,
    @SerializedName("awards_on_streams") var awardsOnStreams: Boolean? = null,
    @SerializedName("mweb_xpromo_modal_listing_click_daily_dismissible_ios") var mwebXpromoModalListingClickDailyDismissibleIos: Boolean? = null,
    @SerializedName("cookie_consent_banner") var cookieConsentBanner: Boolean? = null,
    @SerializedName("modlog_copyright_removal") var modlogCopyrightRemoval: Boolean? = null,
    @SerializedName("show_nps_survey") var showNpsSurvey: Boolean? = null,
    @SerializedName("do_not_track") var doNotTrack: Boolean? = null,
    @SerializedName("images_in_comments") var imagesInComments: Boolean? = null,
    @SerializedName("mod_service_mute_reads") var modServiceMuteReads: Boolean? = null,
    @SerializedName("chat_user_settings") var chatUserSettings: Boolean? = null,
    @SerializedName("use_pref_account_deployment") var usePrefAccountDeployment: Boolean? = null,
    @SerializedName("mweb_xpromo_interstitial_comments_ios") var mwebXpromoInterstitialCommentsIos: Boolean? = null,
    @SerializedName("mweb_xpromo_modal_listing_click_daily_dismissible_android") var mwebXpromoModalListingClickDailyDismissibleAndroid: Boolean? = null,
    @SerializedName("premium_subscriptions_table") var premiumSubscriptionsTable: Boolean? = null,
    @SerializedName("mweb_xpromo_interstitial_comments_android") var mwebXpromoInterstitialCommentsAndroid: Boolean? = null,
    @SerializedName("crowd_control_for_post") var crowdControlForPost: Boolean? = null,
    @SerializedName("mweb_nsfw_xpromo") var mwebNsfwXpromo: MwebNsfwXpromo? = MwebNsfwXpromo(),
    @SerializedName("chat_group_rollout") var chatGroupRollout: Boolean? = null,
    @SerializedName("resized_styles_images") var resizedStylesImages: Boolean? = null,
    @SerializedName("noreferrer_to_noopener") var noreferrerToNoopener: Boolean? = null,
    @SerializedName("mweb_xpromo_revamp_v3") var mwebXpromoRevampV3: MwebXpromoRevampV3? = MwebXpromoRevampV3()

)