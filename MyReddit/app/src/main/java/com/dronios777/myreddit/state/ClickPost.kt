package com.dronios777.myreddit.state

enum class ClickPost(val dir: String? = null) {
    VOTE_ADD("1"), VOTE_MINUS("-1"), VOTE_ZERO("0"),
    COMMENTS, POST_AUTHOR
}