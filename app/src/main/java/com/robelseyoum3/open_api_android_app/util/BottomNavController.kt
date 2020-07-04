package com.robelseyoum3.open_api_android_app.util

import android.app.Activity
import android.content.Context
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.robelseyoum3.open_api_android_app.R

class BottomNavController(
    val context: Context,
    @IdRes val containerId: Int, //id from activity_main xml of FrameLayout main_nav_host_fragment
    @IdRes val appStartDestinationId: Int,
    val graphChangeListener: OnNavigationGraphChanged?,
    val navGraphProvider: NavGraphProvider
) {
    private val TAG: String = "AppDebug"
    lateinit var activity: Activity
    lateinit var fragmentManager: FragmentManager
    lateinit var navItemChangeListener: OnNavigationItemChanged
    private val navigationBackStack = BackStack.of(appStartDestinationId)

    //For setting the checked icon in the bottom nav
    interface  OnNavigationItemChanged{
        fun onItemChanged(itemId: Int)
    }

    init {
        if(context is Activity)
        {
            activity = context
            fragmentManager = (activity as FragmentActivity).supportFragmentManager
        }
    }

    fun onNavigationItemSelected(itemId: Int = navigationBackStack.last()): Boolean {

        //Replace fragment representing a navigation item
        val fragment = fragmentManager.findFragmentByTag(itemId.toString())
            ?: NavHostFragment.create(navGraphProvider.getNavGraphId(itemId))
        fragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.fade_out
            )
            .replace(containerId, fragment, itemId.toString())
            .addToBackStack(null)
            .commit()

        //Add to back stack
        navigationBackStack.moveLast(itemId)

        //update checked icon
        navItemChangeListener.onItemChanged(itemId)


        //communicate with Activity
        graphChangeListener?.onGraphChange()

        return true
    }

    fun onBackPressed(){
        val childFragmentManager = fragmentManager.findFragmentById(containerId)!!
            .childFragmentManager
        when{
            // We should always try to go back on the child fragment manager stack before going to
            // the navigation stack. It's important to use the child fragment manager instead of the
            // NavController because if the user change tabs super fast commit of the
            // supportFragmentManager may mess up with the NavController child fragment manager back
            // stack

            childFragmentManager.popBackStackImmediate() -> {

            }

            //Fragment back stack is empty so try to back on the navigation stack
            navigationBackStack.size > 1 -> {
                //Remove last item from backstack
                navigationBackStack.removeLast()

                //update the container with new fragment
                onNavigationItemSelected()
            }

            //If the stack has only one and it's not the navigation home we should
            //ensure that the application always leave from startDestination
            navigationBackStack.last() != appStartDestinationId -> {
                navigationBackStack.removeLast()
                navigationBackStack.add(0, appStartDestinationId)
                onNavigationItemSelected()
            }

            else -> activity.finish()

        }
    }

    private class BackStack: ArrayList<Int>(){
        companion object {
            fun of(vararg  elements: Int): BackStack {
                val b = BackStack()
                b.addAll(elements.toTypedArray())
                return b
            }
        }

        fun removeLast() = removeAt(size - 1)

        fun moveLast(item: Int){
            remove(item)
            add(item)
        }
    }

    fun setOnItemNavigationChanged(listener: (itemId: Int) -> Unit){
        navItemChangeListener = object: OnNavigationItemChanged {
            override fun onItemChanged(itemId: Int) {
                listener.invoke(itemId)
            }
        }
    }

    //Get id of each graph
    //ex: R.navigation.nav_blog
    //ex: R.navigation.nav_create_blog
    interface NavGraphProvider{
        @NavigationRes
        fun getNavGraphId(itemId: Int): Int
    }


    //Execute when navigation graph changes
    //ex: Select a new item on the bottom nav
    //ex: Home -> Account
    interface OnNavigationGraphChanged {
        fun onGraphChange()
    }

    interface OnNavigationReselectedListener  {
        fun onReselectNavItem(navController: NavController, fragment: Fragment)
    }

}

//convenience extension to set up the navigation
fun BottomNavigationView.setUpNavigation(
    bottomNavController: BottomNavController,
    onReselectListener: BottomNavController.OnNavigationReselectedListener
){
    setOnNavigationItemReselectedListener {
        bottomNavController.onNavigationItemSelected(it.itemId)
    }

    setOnNavigationItemReselectedListener {
        bottomNavController
            .fragmentManager
            .findFragmentById(bottomNavController.containerId)!!
            .childFragmentManager
            .fragments[0]?.let { fragment ->

            onReselectListener.onReselectNavItem(
                bottomNavController.activity.findNavController(bottomNavController.containerId),
                fragment
            )
        }
    }
    //highlighting the icon
    bottomNavController.setOnItemNavigationChanged { itemId ->
        menu.findItem(itemId).isChecked = true
    }

}