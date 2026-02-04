package dev.carlosivis.workoutsmart.screens.profile

sealed class ProfileViewAction {
    object GoogleLogin : ProfileViewAction()
    object Navigate{
        object Back : ProfileViewAction()
        object Settings: ProfileViewAction()
    }
    object CleanError: ProfileViewAction()
    object Logout: ProfileViewAction()
    object GetUserProfile: ProfileViewAction()
}
