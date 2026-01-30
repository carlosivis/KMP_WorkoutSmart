package dev.carlosivis.workoutsmart.data.local.datasource

import com.russhwolf.settings.Settings

class UserLocalDataSourceImpl(
    private val settings: Settings
) : UserLocalDataSource {

    private companion object {
        const val KEY_USER_TOKEN = "user_token"
    }

    override fun saveUserToken(token: String) {
        settings.putString(KEY_USER_TOKEN, token)
    }

    override fun getUserToken(): String? {
        return settings.getStringOrNull(KEY_USER_TOKEN)
    }

    override fun clearUserData() {
        settings.remove(KEY_USER_TOKEN)
    }
}