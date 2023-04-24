package com.dronios777.myreddit.data.repository.paging_sources

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dronios777.myreddit.FIRST_PAGE
import com.dronios777.myreddit.data.model.posts.SubredditPostsResponse.Data.Posts
import com.dronios777.myreddit.data.repository.Repository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PostPagingSource @Inject constructor(private val repository: Repository) :
    PagingSource<String, Posts>() {

    private lateinit var subredditName: String
    fun subInit(subName: String) {
        subredditName = subName
    }

    private var key: String? =
        null //для проверки на конец списка страниц (сервер шлёт данные бессконечно, рекурсией)

    override fun getRefreshKey(state: PagingState<String, Posts>): String? = null

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Posts> {
        val page = params.key ?: FIRST_PAGE

        return try {
            repository.getSubredditPosts(subredditName, page).let {

                if (key == it.data?.after.toString().trim()) {
                    LoadResult.Page(
                        data = emptyList(),
                        prevKey = null,
                        nextKey = null
                    )
                } else {
                    key = it.data?.after.toString().trim()
                    LoadResult.Page(
                        data = it.data?.posts!!,
                        prevKey = null,
                        nextKey = it.data.after ?: FIRST_PAGE
                    )
                }
            }

        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}