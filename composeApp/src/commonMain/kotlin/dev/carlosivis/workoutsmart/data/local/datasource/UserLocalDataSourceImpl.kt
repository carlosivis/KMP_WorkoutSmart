package dev.carlosivis.workoutsmart.data.local.datasource

import com.russhwolf.settings.Settings
import dev.carlosivis.workoutsmart.models.UserResponse
import kotlinx.serialization.json.Json

class UserLocalDataSourceImpl(
    private val settings: Settings
) : UserLocalDataSource {

    private companion object {
        const val KEY_USER_TOKEN = "user_token"
        const val KEY_USER_DATA = "user_data"
    }

    override fun saveUserToken(token: String) {
        settings.putString(KEY_USER_TOKEN, token)
    }

    override fun getUserToken(): String? {
        return settings.getStringOrNull(KEY_USER_TOKEN)
    }

    override fun clearUserData() {
        settings.remove(KEY_USER_TOKEN)
        settings.remove(KEY_USER_DATA)
    }

    override fun saveUser(user: UserResponse) {
        val json = Json.encodeToString(user)
        settings.putString(KEY_USER_DATA, json)
    }

    override fun getUser(): UserResponse? {
        val json = settings.getStringOrNull(KEY_USER_DATA) ?: return null
        return try {
            Json.decodeFromString<UserResponse>(json)
        } catch (e: Exception) {
            null
        }
    }
}