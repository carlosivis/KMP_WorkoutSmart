package dev.carlosivis.workoutsmart.plataform

import cocoapods.FirebaseCore.FIRApp
import dev.gitlive.firebase.FirebaseApp
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.UIKit.UIApplication
import kotlin.coroutines.resumeWithException

class GoogleAuthProviderIOS : GoogleAuthProvider {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun signIn(): GoogleAuthResult =
        suspendCancellableCoroutine { cont ->

            val clientId = FIRApp.defaultApp()
                ?.options
                ?.clientID
                ?: error("Firebase clientID not found")

            val config = GIDConfiguration(clientID = clientId)

            val rootVC = UIApplication.sharedApplication
                .keyWindow
                ?.rootViewController
                ?: error("No rootViewController")

            GIDSignIn.sharedInstance.signInWithConfiguration(
                configuration = config,
                presentingViewController = rootVC
            ) { user, error ->

                if (error != null) {
                    cont.resumeWithException(error)
                    return@signInWithConfiguration
                }

                val idToken = user
                    ?.authentication
                    ?.idToken
                    ?: error("Missing idToken")

                cont.resume(GoogleAuthResult(idToken))
            }
        }
}