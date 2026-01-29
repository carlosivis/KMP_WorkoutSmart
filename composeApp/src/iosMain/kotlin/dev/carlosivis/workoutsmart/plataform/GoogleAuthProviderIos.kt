package dev.carlosivis.workoutsmart.plataform

import cocoapods.GoogleSignIn.GIDSignIn
import dev.gitlive.firebase.auth.AuthCredential
import dev.gitlive.firebase.auth.GoogleAuthProvider
import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.UIApplication
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GoogleAuthProviderIos : dev.carlosivis.workoutsmart.plataform.GoogleAuthProvider {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun getCredential(): AuthCredential? {
        return suspendCoroutine { continuation ->
            val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController

            if (rootViewController == null) {
                continuation.resume(null)
                return@suspendCoroutine
            }

            GIDSignIn.sharedInstance.signInWithPresentingViewController(rootViewController) { result, error ->
                if (error != null) {
                    continuation.resume(null)
                } else {
                    val idToken = result?.user?.idToken?.tokenString
                    val accessToken = result?.user?.accessToken?.tokenString

                    if (idToken != null && accessToken != null) {
                        continuation.resume(GoogleAuthProvider.credential(idToken, accessToken))
                    } else {
                        continuation.resume(null)
                    }
                }
            }
        }
    }
}