package dev.carlosivis.workoutsmart.screens.login

sealed class LoginViewAction {
    object GoogleLogin : LoginViewAction()
    object Navigate{
        object Back : LoginViewAction()
        object Settings: LoginViewAction()
    }
}
