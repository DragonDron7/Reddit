package com.dronios777.myreddit.data.api

import com.dronios777.myreddit.REDIRECT_URI
import com.dronios777.myreddit.data.model.friends.FriendsResponse
import com.dronios777.myreddit.data.model.me.MeResponse
import com.dronios777.myreddit.data.model.posts.CommentsResponse
import com.dronios777.myreddit.data.model.posts.SubredditPostsResponse
import com.dronios777.myreddit.data.model.subreddit.ResponseSubreddits
import com.dronios777.myreddit.data.model.token.ResponseToken
import com.dronios777.myreddit.data.model.user_info.UserInfoResponse
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.*
import java.util.*
import javax.inject.Inject


class ServicesApi @Inject constructor() {
    object RetrofitServices {
        private const val BASE_URL = "https://oauth.reddit.com/"
        private val gson = GsonBuilder().setLenient().create()

        // private val logger = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        //  private val client = OkHttpClient.Builder().addInterceptor(logger)
        private val retrofit = Retrofit.Builder()

            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            // .client(client.build())
            .build()

        val apiToken: ApiToken by lazy { retrofit.create(ApiToken::class.java) }
        val apiSubreddits: ApiSubreddits by lazy { retrofit.create(ApiSubreddits::class.java) }
        val apiSearchSubreddit: ApiSearchSubreddit by lazy { retrofit.create(ApiSearchSubreddit::class.java) }
        val apiJoinOrLeaveSub: ApiJoinOrLeaveSub by lazy { retrofit.create(ApiJoinOrLeaveSub::class.java) }
        val apiUserSubscriptions: ApiUserSubscriptions by lazy {
            retrofit.create(
                ApiUserSubscriptions::class.java
            )
        }
        val apiVote: ApiVote by lazy { retrofit.create(ApiVote::class.java) }
        val apiSubscribePost: ApiSubscribePost by lazy { retrofit.create(ApiSubscribePost::class.java) }
        val apiUser: ApiUser by lazy { retrofit.create(ApiUser::class.java) }


        // interfaces

        //получение токена
        interface ApiToken {
            @POST("https://www.reddit.com/api/v1/access_token")
            suspend fun getToken(
                @Header("Authorization") authHeader: String,
                @Query("grant_type") grantType: String = "authorization_code",
                @Query("code") code: String,
                @Query("redirect_uri") redirectUri: String = REDIRECT_URI,
            ): ResponseToken
        }


        interface ApiSubreddits {
            @GET("subreddits/{where}")
            suspend fun getSubreddits( //получение сабреддитов по параметру (new, popular,...)
                @Header("Authorization") authHeader: String,
                @Path("where") where: String,
                @Query("limit") limit: String = "70"
            ): ResponseSubreddits

            @GET("r/{subreddit}") //получение постов из сабреддита
            suspend fun getSubredditPosts(
                @Header("Authorization") authHeader: String,
                @Path("subreddit") subreddit: String,
                @Query("after") page: String
            ): SubredditPostsResponse

            @GET("comments/{post}")//получение комментариев к посту
            suspend fun getCommentsPost(
                @Header("Authorization") authHeader: String,
                @Path("post") post: String
            ): List<CommentsResponse>
        }

        //посты, на которые подписан user
        interface ApiSubscribePost {
            @GET("/{sorting}")
            suspend fun getSubscribePost(
                @Header("Authorization") authHeader: String,
                @Path("sorting") sorting: String? = "new",
                @Query("after") page: String?
            ): SubredditPostsResponse
        }


        //поиск сабреддитов
        interface ApiSearchSubreddit {
            @GET("/subreddits/search/")
            suspend fun searchSubreddits(
                @Query("q") q: String,
                @Header("Authorization") authHeader: String
            ): ResponseSubreddits
        }

        //подписка/отписка от сабреддита
        interface ApiJoinOrLeaveSub {
            @POST("/api/subscribe")
            suspend fun joinOrLeaveSub(
                @Header("Authorization") authHeader: String,
                @Query("action") action: String,
                @Query("sr_name") sr_name: String
            )
        }

        //получение списка подписок пользователя на сабреддиты
        interface ApiUserSubscriptions {
            @GET("/subreddits/mine/subscriber")
            suspend fun getUserSubscriptions(
                @Header("Authorization") authHeader: String
            ): Response<ResponseSubreddits>
        }

        //голосование
        interface ApiVote {
            @POST("/api/vote")
            suspend fun vote(
                @Header("Authorization") authHeader: String,
                @Query("dir") dir: String = "authorization_code",
                @Query("id") id: String
            )
        }

        interface ApiUser {
            @GET("api/v1/me")
            suspend fun getUserData(@Header("Authorization") authHeader: String): MeResponse

            @GET("/api/v1/me/friends")
            suspend fun getUserFriends(@Header("Authorization") authHeader: String): FriendsResponse

            @GET("user/{username}/about")
            suspend fun user(
                @Header("Authorization") authHeader: String,
                @Path("username") userName: String
            ): UserInfoResponse

            @PUT("/api/v1/me/friends/{username}")
            suspend fun addToFriends(
                @Header("Authorization") authHeader: String,
                @Path("username") username: String,
                @Body requestBody: String,
            )

            @DELETE("/api/v1/me/friends/{username}")
            suspend fun deleteFromFriends(
                @Header("Authorization") authHeader: String,
                @Path("username") userName: String
            ): Response<Unit>

        }
    }
}