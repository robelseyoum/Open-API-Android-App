package com.robelseyoum3.open_api_android_app.ui.auth.state

sealed class AuthStateEvent {

    data class LoginAttemptEvent(
        val email: String,
        val password: String
    ): AuthStateEvent()

    data class RegisterAttemptEvent(
        val email: String,
        val username: String,
        val password: String,
        val confirm_password: String
    ): AuthStateEvent()

    object CheckPreviousAuthEvent : AuthStateEvent()

   class None : AuthStateEvent()

}