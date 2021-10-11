package com.robelseyoum3.open_api_android_app.ui.main.blog.state

sealed class BlogStateEvent {
    class BlogSearchEvent : BlogStateEvent()
    class None: BlogStateEvent()
}
