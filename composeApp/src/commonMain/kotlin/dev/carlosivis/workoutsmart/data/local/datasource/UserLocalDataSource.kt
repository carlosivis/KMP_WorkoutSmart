package dev.carlosivis.workoutsmart.data.local.datasource

import dev.carlosivis.workoutsmart.models.UserResponse

interface UserLocalDataSource {
    fun saveUserToken(token: String)
    fun getUserToken(): String?
    fun clearUserData()
    fun saveUser(user: UserResponse)
    fun getUser(): UserResponse?
}