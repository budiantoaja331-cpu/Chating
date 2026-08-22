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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.example.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val userId: String, val userName: String, val profileImageUrl: String? = null, val isProfileComplete: Boolean = false) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    private val db = FirebaseFirestore.getInstance()

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth?.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
    }

    init {
        Log.d("AuthInit", "AuthViewModel initialized. Checking current user...")
        auth?.addAuthStateListener(authStateListener)
        checkCurrentUser()
    }

    private suspend fun fetchProfileCompletionStatus(uid: String): Boolean {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            if (doc.exists()) {
                doc.getBoolean("isProfileComplete") ?: false
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("AuthInit", "Error fetching profile completion status", e)
            false
        }
    }

    private fun checkCurrentUser() {
        viewModelScope.launch {
            try {
                val user = auth?.currentUser
                if (user != null) {
                    Log.d("AuthInit", "Found existing user: ${user.uid}")
                    val isComplete = fetchProfileCompletionStatus(user.uid)
                    _authState.value = AuthState.Success(user.uid, user.displayName ?: "User", user.photoUrl?.toString(), isComplete)
                } else {
                    Log.d("AuthInit", "No existing user found. Waiting for login.")
                }
            } catch (e: Exception) {
                Log.e("AuthInit", "Exception during checkCurrentUser: ${e.message}", e)
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            Log.d("AuthInit", "Starting Google Sign-In process...")
            _authState.value = AuthState.Loading
            try {
                val credentialManager = CredentialManager.create(context)
                
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .setAutoSelectEnabled(false)
                    .build()
                
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                
                val result = credentialManager.getCredential(context, request)
                val credential = result.credential
                
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = auth?.signInWithCredential(firebaseCredential)?.await()
                    val user = authResult?.user
                    
                    if (user != null) {
                        Log.d("AuthInit", "Firebase sign-in successful. UID: ${user.uid}")
                        val isComplete = fetchProfileCompletionStatus(user.uid)
                        _authState.value = AuthState.Success(user.uid, user.displayName ?: "User", user.photoUrl?.toString(), isComplete)
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
    
    fun markProfileComplete() {
        val current = _authState.value
        if (current is AuthState.Success) {
            _authState.value = current.copy(isProfileComplete = true)
        }
    }
    
    fun signOut() {
        Log.d("AuthInit", "Signing out user.")
        auth?.signOut()
        _authState.value = AuthState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        auth?.removeAuthStateListener(authStateListener)
    }
}
