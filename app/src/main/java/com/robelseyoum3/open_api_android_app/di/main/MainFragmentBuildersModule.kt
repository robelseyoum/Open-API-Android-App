package com.robelseyoum3.open_api_android_app.di.main

import com.robelseyoum3.open_api_android_app.ui.main.account.AccountFragment
import com.robelseyoum3.open_api_android_app.ui.main.account.ChangePasswordFragment
import com.robelseyoum3.open_api_android_app.ui.main.account.UpdateAccountFragment
import com.robelseyoum3.open_api_android_app.ui.main.blog.BlogFragment
import com.robelseyoum3.open_api_android_app.ui.main.blog.UpdateBlogFragment
import com.robelseyoum3.open_api_android_app.ui.main.blog.ViewBlogFragment
import com.robelseyoum3.open_api_android_app.ui.main.create_blog.CreateBlogFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainFragmentBuildersModule {

    @ContributesAndroidInjector()
    abstract fun contributeBlogFragment(): BlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeAccountFragment(): AccountFragment

    @ContributesAndroidInjector()
    abstract fun contributeChangePasswordFragment(): ChangePasswordFragment

    @ContributesAndroidInjector()
    abstract fun contributeCreateBlogFragment(): CreateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateBlogFragment(): UpdateBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeViewBlogFragment(): ViewBlogFragment

    @ContributesAndroidInjector()
    abstract fun contributeUpdateAccountFragment(): UpdateAccountFragment
}