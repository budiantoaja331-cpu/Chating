package com.example

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    private val db: FirebaseFirestore? = try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(auth?.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        _currentUser.value = firebaseAuth.currentUser
    }

    init {
        Log.d("AuthInit", "AuthViewModel initialized. Checking current user...")
        if (auth == null || db == null) {
            _authState.value = AuthState.Error("Firebase gagal diinisialisasi. Pastikan konfigurasi google-services.json ada.")
        } else {
            auth.addAuthStateListener(authStateListener)
            checkCurrentUser()
        }
    }

    private suspend fun fetchProfileCompletionStatus(uid: String): Boolean {
        if (db == null) return false
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
                    val isComplete = fetchProfileCompletionStatus(user.uid)
                    _authState.value = AuthState.Success(user.uid, user.displayName ?: "User", user.photoUrl?.toString(), isComplete)
                }
            } catch (e: Exception) {
                Log.e("AuthInit", "Exception during checkCurrentUser: ${e.message}", e)
            }
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val activity = context.findActivity()
                if (activity == null) {
                    _authState.value = AuthState.Error("Gagal menemukan Activity untuk Google Sign-In")
                    return@launch
                }

                val credentialManager = CredentialManager.create(activity)
                
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .setAutoSelectEnabled(false)
                    .build()
                
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                
                val result = credentialManager.getCredential(activity, request)
                val credential = result.credential
                
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                    val authResult = auth?.signInWithCredential(firebaseCredential)?.await()
                    val user = authResult?.user
                    
                    if (user != null) {
                        val isComplete = fetchProfileCompletionStatus(user.uid)
                        _authState.value = AuthState.Success(user.uid, user.displayName ?: "User", user.photoUrl?.toString(), isComplete)
                    } else {
                        _authState.value = AuthState.Error("Login gagal: Data pengguna kosong")
                    }
                } else {
                    _authState.value = AuthState.Error("Tipe kredensial tidak valid")
                }
            } catch (e: GetCredentialException) {
                Log.e("AuthInit", "GetCredentialException caught: ${e.message}", e)
                val msg = if (e.localizedMessage?.contains("10") == true || e.type.contains("10") || e.message?.contains("DEVELOPER_ERROR") == true) {
                    "Gagal Login (Error 10): SHA-1 Keystore Release belum didaftarkan di Firebase Console. Pastikan SHA-1 Github terdaftar."
                } else if (e.localizedMessage?.contains("28433") == true || e.localizedMessage?.contains("CANCELED") == true || e.type.contains("CANCELED")) {
                    "Proses login dibatalkan."
                } else {
                    "Gagal autentikasi Google: ${e.localizedMessage}"
                }
                _authState.value = AuthState.Error(msg)
            } catch (e: Throwable) {
                Log.e("AuthInit", "Unexpected Exception in signInWithGoogle: ${e.message}", e)
                if (e is kotlinx.coroutines.CancellationException) {
                    throw e
                }
                _authState.value = AuthState.Error("Kesalahan tidak terduga: ${e.localizedMessage}")
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
        auth?.signOut()
        _authState.value = AuthState.Idle
    }

    override fun onCleared() {
        super.onCleared()
        auth?.removeAuthStateListener(authStateListener)
    }
}
