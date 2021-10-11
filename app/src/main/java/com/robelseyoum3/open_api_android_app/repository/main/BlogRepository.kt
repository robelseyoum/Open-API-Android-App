package com.robelseyoum3.open_api_android_app.repository.main

import com.robelseyoum3.open_api_android_app.api.main.OpenApiMainService
import com.robelseyoum3.open_api_android_app.persistence.BlogPostDao
import com.robelseyoum3.open_api_android_app.repository.JobManager
import com.robelseyoum3.open_api_android_app.session.SessionManager
import javax.inject.Inject

class BlogRepository
@Inject
constructor(
    val openApiMainService: OpenApiMainService,
    val blogPostDao: BlogPostDao,
    val sessionManager: SessionManager
): JobManager("BlogRepository")
{

    private val TAG: String = "AppDebug"


}
