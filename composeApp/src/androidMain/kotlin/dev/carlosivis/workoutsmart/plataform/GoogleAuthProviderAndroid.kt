package dev.carlosivis.workoutsmart.plataform

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dev.carlosivis.workoutsmart.R

class GoogleAuthProviderAndroid(
    private val context: Context
) : GoogleAuthProvider {

    private val credentialManager = CredentialManager.create(context)

    override suspend fun signIn(): GoogleAuthResult {
        val request = GetCredentialRequest(
            listOf(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(
                        context.getString(R.string.default_web_client_id)
                    )
                    .build()
            )
        )

        val result = credentialManager.getCredential(
            context = context,
            request = request
        )

        val credential = result.credential

        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleIdTokenCredential =
                GoogleIdTokenCredential.createFrom(credential.data)

            return GoogleAuthResult(
                idToken = googleIdTokenCredential.idToken
            )
        } else {
            error("Unexpected credential type")
        }
    }
}