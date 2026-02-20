package dev.carlosivis.workoutsmart.platform

import dev.gitlive.firebase.auth.AuthCredential

interface GoogleAuthProvider {
    suspend fun getCredential(): AuthCredential?
}
