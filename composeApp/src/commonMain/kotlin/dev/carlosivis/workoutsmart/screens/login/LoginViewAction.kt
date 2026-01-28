package dev.carlosivis.workoutsmart.screens.login

sealed class LoginViewAction {
    object GoogleLogin : LoginViewAction()
    object NavigateBack : LoginViewAction()
}
