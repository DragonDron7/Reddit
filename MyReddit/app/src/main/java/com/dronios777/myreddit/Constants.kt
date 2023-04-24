package com.dronios777.myreddit

const val CLIENT_ID = "vB3Fv7SqAC14XngO5BGyAw"
const val CLIENT_SECRET = ""
const val RESPONSE_TYPE = "code"
const val STATE = "777"
const val REDIRECT_URI = "project://humblr777_reddit/dronios777"
const val DURATION = "permanent"

const val SCOPE = "identity edit flair history modconfig modflair modlog modposts " +
        "modwiki mysubreddits privatemessages read report save submit subscribe " +
        "vote wikiedit wikiread"

const val PATH =
    "https://www.reddit.com/api/v1/authorize.compact" +
            "?client_id=" + CLIENT_ID +
            "&response_type=" + RESPONSE_TYPE +
            "&state=" + STATE +
            "&redirect_uri=" + REDIRECT_URI +
            "&duration=" + DURATION +
            "&scope=" + SCOPE

const val AUTH_STRING = "$CLIENT_ID:$CLIENT_SECRET"
const val EMPTY = ""

//кол-во экранов онбординга
const val NUMBER_SCREENS_ONBOARDING = 3

//хранилище
const val APP_PREFERENCES = "LastToken" //имя файла хранения токена
const val KEY_STRING = "Token" //токен

//поиск сабреддитов по типу
const val NEW = "new"
const val POPULAR = "popular"

//подписка на сабреддит
const val SUB = "sub"

//отписка от сабреддита
const val UNSUB = "unsub"

//для того чтобы поделиться
const val SHARE =
    "?utm_source=share&utm_medium=android_app&utm_name=androidcss&utm_term=14&utm_content=share_button"

//какая кнопка нажата во вкладке "Избранное"
const val BTN_SUB = "btnSub"
const val BTN_POSTS = "btnPosts"

//формат даты
const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

//пагинация
const val PAGE_SIZE = 10
const val FIRST_PAGE = ""

