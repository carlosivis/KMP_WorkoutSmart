package dev.carlosivis.workoutsmart.plataform

import dev.gitlive.firebase.auth.AuthCredential

interface GoogleAuthProvider {
    suspend fun getCredential(): AuthCredential?
}
