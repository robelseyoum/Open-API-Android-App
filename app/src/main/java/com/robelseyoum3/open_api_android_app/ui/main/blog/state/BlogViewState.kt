package com.robelseyoum3.open_api_android_app.ui.main.blog.state

import com.robelseyoum3.open_api_android_app.model.BlogPost

data class BlogViewState (
    // BlogFragment vars
    var blogFields: BlogFields = BlogFields()


    // ViewBlogFragment vars


    // UpdateBlogFragment vars
)
{
    data class BlogFields(
        var blogList: List<BlogPost> = ArrayList<BlogPost>(),
        var searchQuery: String = ""
    )

}