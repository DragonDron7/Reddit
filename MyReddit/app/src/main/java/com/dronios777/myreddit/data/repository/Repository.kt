package com.dronios777.myreddit.data.repository

import com.dronios777.myreddit.data.api.ServicesApi.RetrofitServices
import com.dronios777.myreddit.data.model.friends.FriendsResponse
import com.dronios777.myreddit.data.model.me.MeResponse
import com.dronios777.myreddit.data.model.posts.CommentsResponse
import com.dronios777.myreddit.data.model.posts.SubredditPostsResponse
import com.dronios777.myreddit.data.model.subreddit.ResponseSubreddits
import com.dronios777.myreddit.data.model.user_info.UserInfoResponse
import com.dronios777.myreddit.data.storage.Token
import retrofit2.Response
import javax.inject.Inject

class Repository @Inject constructor(private val storageToken: Token) {

    //считываем токен из хранилища
    fun getTokenStorage(): String? {
        return storageToken.getDataFromSharedPreference()
    }

    //записываем токен в хранилище
    fun saveTokenStorage(tokenKey: String) =
        storageToken.saveDataToSharedPreference(tokenKey)


    //--------------------------------сабреддиты---------------------------------
    //выборка сабреддитов по параметру
    suspend fun getSubreddits(where: String): ResponseSubreddits {
        return RetrofitServices.apiSubreddits.getSubreddits(
            authHeader = "Bearer ${getTokenStorage()}",
            where = where
        )
    }

    //поиск сабреддитов
    suspend fun searchSubreddits(query: String): ResponseSubreddits {
        return RetrofitServices.apiSearchSubreddit.searchSubreddits(
            q = query,
            authHeader = "Bearer ${getTokenStorage()}"
        )
    }

    //подписка/отписка на сабреддит
    suspend fun joinOrLeaveSub(action: String, subName: String) {
        RetrofitServices.apiJoinOrLeaveSub.joinOrLeaveSub(
            authHeader = "Bearer ${getTokenStorage()}",
            action = action,
            sr_name = subName
        )
    }

    //получение списка подписок
    suspend fun getUserSubscriptions(): Response<ResponseSubreddits> {
        return RetrofitServices.apiUserSubscriptions.getUserSubscriptions(
            authHeader = "Bearer ${getTokenStorage()}"
        )
    }

    //--------------------------------посты---------------------------------
    //получение списка постов для конкретного сабреддита
    suspend fun getSubredditPosts(subredditName: String, page: String): SubredditPostsResponse {
        return RetrofitServices.apiSubreddits.getSubredditPosts(
            authHeader = "Bearer ${getTokenStorage()}",
            subreddit = subredditName,
            page = page
        )
    }

    //посты из сообществ, на которые подписан user
    suspend fun subscribePost(page: String): SubredditPostsResponse {
        return RetrofitServices.apiSubscribePost.getSubscribePost(
            authHeader = "Bearer ${getTokenStorage()}",
            page = page
        )
    }

    //голосование за пост
    suspend fun vote(id: String, dir: String) {
        RetrofitServices.apiVote.vote(
            authHeader = "Bearer ${getTokenStorage()}",
            dir = dir,//(1) увеличить рейтинг, (-1) уменьшить, (0) отменить
            id = id //это параметр "name" в посте
        )
    }

    //получение списка комментариев для конкретного поста
    suspend fun getCommentsPost(postID: String): List<CommentsResponse> {
        return RetrofitServices.apiSubreddits.getCommentsPost(
            authHeader = "Bearer ${getTokenStorage()}",
            post = postID
        )
    }

    //--------------------------------мой профиль---------------------------------
    suspend fun myInfo(): MeResponse {
        return RetrofitServices.apiUser.getUserData(authHeader = "Bearer ${getTokenStorage()}")
    }

    suspend fun myFriends(): FriendsResponse {
        return RetrofitServices.apiUser.getUserFriends(authHeader = "Bearer ${getTokenStorage()}")
    }

    //-------------------------------информация о другом пользователе----------------------------
    suspend fun userInfo(userName: String): UserInfoResponse {
        return RetrofitServices.apiUser.user(
            authHeader = "Bearer ${getTokenStorage()}",
            userName = userName
        )
    }

    suspend fun addToFriends(userName: String) {
        RetrofitServices.apiUser.addToFriends(
            authHeader = "Bearer ${getTokenStorage()}",
            userName,
            "{\"name\": \"$userName\"}"
        )
    }

    suspend fun removeFriend(userName: String) {
        RetrofitServices.apiUser.deleteFromFriends(
            authHeader = "Bearer ${getTokenStorage()}",
            userName
        )
    }

}