package com.robelseyoum3.open_api_android_app.ui.main.account

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.robelseyoum3.open_api_android_app.R
import com.robelseyoum3.open_api_android_app.model.AccountProperties
import com.robelseyoum3.open_api_android_app.session.SessionManager
import com.robelseyoum3.open_api_android_app.ui.main.account.state.AccountStateEvent
import kotlinx.android.synthetic.main.fragment_account.*
import javax.inject.Inject

class AccountFragment : BaseAccountFragment(){

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        change_password.setOnClickListener {
            findNavController().navigate(R.id.action_accountFragment_to_changePasswordFragment)
        }

        logout_button.setOnClickListener {
            viewModel.logOut()
        }

        subscribeObservers()
    }

    private fun subscribeObservers(){
        //here we observe the data state
        viewModel.dataState.observe(viewLifecycleOwner, Observer {dataState ->
            stateChangeListener.onDataStateChange(dataState)
            dataState?.let {
                it.data?.let { data ->
                    data.data?.let { event ->
                        event.getContentIfNotHandled()?.let { accountViewState ->
                            accountViewState.accountProperties?.let {accountProperties ->
                                Log.d(TAG, "AccountFragment, DataState: $accountProperties")
                                viewModel.setAccountPropertiesData(accountProperties) //this will be used to view get observed
                            }
                        }
                    }
                }
            }
        })

        //once the data state is observed it will assign the value to view state and  here we observe the view state
        viewModel.viewState.observe(viewLifecycleOwner, Observer { viewState ->
            viewState?.let { accountViewState ->
                accountViewState.accountProperties?.let { accountProperties ->
                    Log.d(TAG, "AccountFragment, ViewState: $accountProperties")
                    setAccountDataFields(accountProperties)
                }
            }
        })
    }

    private fun setAccountDataFields(accountProperties: AccountProperties){
        email?.text = accountProperties.email
        username?.text = accountProperties.username
    }

    override fun onResume() {
        super.onResume()
        //this will update the cache in the background
        viewModel.setStateEvent(
            AccountStateEvent.GetAccountPropertiesEvent
        )
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.edit_view_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.edit -> {
                findNavController().navigate(R.id.action_accountFragment_to_updateAccountFragment)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}