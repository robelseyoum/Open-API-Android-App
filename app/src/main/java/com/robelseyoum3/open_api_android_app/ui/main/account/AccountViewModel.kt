package com.robelseyoum3.open_api_android_app.ui.main.account

import androidx.lifecycle.LiveData
import com.robelseyoum3.open_api_android_app.model.AccountProperties
import com.robelseyoum3.open_api_android_app.repository.main.AccountRepository
import com.robelseyoum3.open_api_android_app.session.SessionManager
import com.robelseyoum3.open_api_android_app.ui.BaseViewModel
import com.robelseyoum3.open_api_android_app.ui.DataState
import com.robelseyoum3.open_api_android_app.ui.auth.state.AuthStateEvent
import com.robelseyoum3.open_api_android_app.ui.main.account.state.AccountStateEvent
import com.robelseyoum3.open_api_android_app.ui.main.account.state.AccountStateEvent.*
import com.robelseyoum3.open_api_android_app.ui.main.account.state.AccountViewState
import com.robelseyoum3.open_api_android_app.util.AbsentLiveData
import javax.inject.Inject

class AccountViewModel @Inject constructor(
    val sessionManager: SessionManager,
    val accountRepository: AccountRepository
): BaseViewModel<AccountStateEvent, AccountViewState>(){

    override fun handleStateEvent(stateEvent: AccountStateEvent): LiveData<DataState<AccountViewState>> {
        when(stateEvent){

            is GetAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.getAccountProperties(authToken)
                }?: AbsentLiveData.create()
            }

            is UpdateAccountPropertiesEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    authToken.account_pk?.let { pk ->
                        accountRepository.saveAccountProperties(
                            authToken,
                            AccountProperties(
                                pk,
                                stateEvent.email,
                                stateEvent.username
                            )
                        )
                    }
                }?: AbsentLiveData.create()
            }

            is ChangePasswordEvent -> {
                return sessionManager.cachedToken.value?.let { authToken ->
                    accountRepository.updatePassword(
                        authToken,
                        stateEvent.currentPassword,
                        stateEvent.newPassword,
                        stateEvent.confirmNewPassword)
                }?: AbsentLiveData.create()
            }

            is None -> return AbsentLiveData.create()
        }
    }

    override fun initNewViewState(): AccountViewState {
        return  AccountViewState()
    }

    fun setAccountPropertiesData(accountProperties: AccountProperties){
        val update = getCurrentViewStateOrNew()
        if(update.accountProperties == accountProperties){
            return
        }
        update.accountProperties = accountProperties
        _viewState.value = update
    }

    fun logOut(){
        sessionManager.logout()
    }

    fun cancelActiveJobs() {
        handlePendingData()
        accountRepository.cancelActiveJobs()
    }

    private fun handlePendingData(){
        setStateEvent(None)
    }
}