package com.example

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String, val userName: String, val profileImageUrl: String? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        Log.d("AuthInit", "AuthViewModel initialized. Checking current user...")
        checkCurrentUser()
    }

    private fun checkCurrentUser() {
        try {
            val user = auth?.currentUser
            if (user != null) {
                Log.d("AuthInit", "Found existing user: ${user.uid}")
                _authState.value = AuthState.Success(user.uid, user.displayName ?: "User", user.photoUrl?.toString())
            } else {
                Log.d("AuthInit", "No existing user found. Waiting for login.")
            }
        } catch (e: Exception) {
            Log.e("AuthInit", "Exception during checkCurrentUser: ${e.message}", e)
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            Log.d("AuthInit", "Starting Google Sign-In process...")
            _authState.value = AuthState.Loading
            try {
                Log.d("AuthInit", "Creating CredentialManager...")
                val credentialManager = CredentialManager.create(context)
                
                Log.d("AuthInit", "Building GetGoogleIdOption using Client ID: ${BuildConfig.GOOGLE_WEB_CLIENT_ID}")
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .setAutoSelectEnabled(false)
                    .build()
                
                Log.d("AuthInit", "Building GetCredentialRequest...")
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                
                Log.d("AuthInit", "Invoking credentialManager.getCredential()...")
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                
                Log.d("AuthInit", "Received credential result type: ${credential::class.java.simpleName}")
                
                if (credential is GoogleIdTokenCredential) {
                    Log.d("AuthInit", "Credential is GoogleIdTokenCredential. Retrieving ID token...")
                    val idToken = credential.idToken
                    
                    Log.d("AuthInit", "Retrieving Firebase credential via GoogleAuthProvider...")
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    
                    Log.d("AuthInit", "Signing in to FirebaseAuth with credential...")
                    val authResult = auth?.signInWithCredential(firebaseCredential)?.await()
                    val user = authResult?.user
                    
                    if (user != null) {
                        Log.d("AuthInit", "Firebase sign-in successful. UID: ${user.uid}")
                        _authState.value = AuthState.Success(user.uid, user.displayName ?: "User", user.photoUrl?.toString())
                    } else {
                        Log.e("AuthInit", "Firebase sign-in failed: Data pengguna kosong.")
                        _authState.value = AuthState.Error("Login gagal: Data pengguna kosong")
                    }
                } else {
                    Log.e("AuthInit", "Unknown credential type received.")
                    _authState.value = AuthState.Error("Tipe kredensial tidak valid")
                }
            } catch (e: GetCredentialException) {
                Log.e("AuthInit", "GetCredentialException caught: ${e.message}", e)
                _authState.value = AuthState.Error("Gagal memanggil Google Sign-In: ${e.message}")
            } catch (e: Exception) {
                Log.e("AuthInit", "Unexpected Exception in signInWithGoogle: ${e.message}", e)
                _authState.value = AuthState.Error("Kesalahan tidak terduga: ${e.message}")
            }
        }
    }
    
    fun signOut() {
        Log.d("AuthInit", "Signing out user.")
        auth?.signOut()
        _authState.value = AuthState.Idle
    }
}
