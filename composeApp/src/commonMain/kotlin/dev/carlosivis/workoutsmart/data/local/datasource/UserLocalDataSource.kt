package dev.carlosivis.workoutsmart.data.local.datasource

interface UserLocalDataSource {
    fun saveUserToken(token: String)
    fun getUserToken(): String?
    fun clearUserData()
}