package com.example.repository

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.example.R
import com.example.BuildConfig
import com.example.model.User
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import android.util.Log

class AuthRepository (
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
                _currentUser.value = user
                saveUserToFirestore(user)
            } else {
                _currentUser.value = null
            }
        }
    }

    suspend fun signInWithGoogle(context: Context): Result<User> {
        return try {
            val credentialManager = CredentialManager.create(context)
            val webClientId = context.getString(com.example.R.string.default_web_client_id)

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(true)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(context, request)
            handleSignInResult(result)
        } catch (e: androidx.credentials.exceptions.NoCredentialException) {
            Log.e("AuthRepository", "No Google account found on device", e)
            Result.failure(Exception("No Google account found on device. Please sign in via device settings."))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Google Sign In Failed", e)
            Result.failure(e)
        }
    }

    private suspend fun handleSignInResult(result: GetCredentialResponse): Result<User> {
        val credential = result.credential
        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            val authCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
            val authResult = auth.signInWithCredential(authCredential).await()
            
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
                saveUserToFirestore(user)
                return Result.success(user)
            } else {
                return Result.failure(Exception("Firebase user is null"))
            }
        }
        return Result.failure(Exception("Invalid credential type"))
    }

    private fun saveUserToFirestore(user: User) {
        firestore.collection("users").document(user.id).set(user)
    }

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: email.substringBefore("@"),
                    email = firebaseUser.email ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
                saveUserToFirestore(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Firebase user is null"))
            }
        } catch (e: com.google.firebase.auth.FirebaseAuthInvalidCredentialsException) {
            Log.e("AuthRepository", "Email Sign In Failed - Invalid Credentials", e)
            Result.failure(Exception("Invalid email or password. If you don't have an account, please sign up first."))
        } catch (e: Exception) {
            Log.e("AuthRepository", "Email Sign In Failed", e)
            Result.failure(e)
        }
    }

    suspend fun signUpWithEmail(email: String, password: String): Result<User> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user
            if (firebaseUser != null) {
                val user = User(
                    id = firebaseUser.uid,
                    name = firebaseUser.displayName ?: email.substringBefore("@"),
                    email = firebaseUser.email ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
                saveUserToFirestore(user)
                Result.success(user)
            } else {
                Result.failure(Exception("Firebase user is null"))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Email Sign Up Failed", e)
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }
}
