package dev.carlosivis.workoutsmart.data.local.datasource

import com.russhwolf.settings.Settings
import dev.carlosivis.workoutsmart.models.UserResponse
import kotlinx.serialization.json.Json

class UserLocalDataSourceImpl(
    private val settings: Settings
) : UserLocalDataSource {

    private val jsonSerializer = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        isLenient = true
        coerceInputValues = true
    }

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
        try {
            val jsonString = jsonSerializer.encodeToString(user)
            settings.putString(KEY_USER_DATA, jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getUser(): UserResponse? {
        val jsonString = settings.getStringOrNull(KEY_USER_DATA)
        if (jsonString.isNullOrBlank()) {
            return null
        }
        return try {
            val user = jsonSerializer.decodeFromString<UserResponse>(jsonString)
            user
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}