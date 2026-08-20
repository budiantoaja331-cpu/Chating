package com.example

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class NearbyUser(
    val id: String = "",
    val name: String = "",
    val avatarUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val distanceMeters: Float = 0f
)

sealed class NearbyUiState {
    object Loading : NearbyUiState()
    object PermissionRequired : NearbyUiState()
    data class Success(val nearbyUsers: List<NearbyUser>) : NearbyUiState()
    data class Error(val message: String) : NearbyUiState()
}

class NearbyViewModel(private val userId: String = "my_user_id", private val myName: String = "My User") : ViewModel() {

    private val _uiState = MutableStateFlow<NearbyUiState>(NearbyUiState.PermissionRequired)
    val uiState: StateFlow<NearbyUiState> = _uiState.asStateFlow()

    private val database = FirebaseDatabase.getInstance()
    private val locationsRef = database.getReference("locations")

    private var currentLocation: Location? = null

    fun requirePermission() {
        _uiState.value = NearbyUiState.PermissionRequired
    }

    @SuppressLint("MissingPermission")
    fun fetchLocationAndNearbyUsers(context: Context) {
        _uiState.value = NearbyUiState.Loading

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        
        viewModelScope.launch {
            try {
                // Get current location
                val location = fusedLocationClient.lastLocation.await()
                if (location != null) {
                    currentLocation = location
                    
                    // Update our own location in Firebase Realtime DB
                    val myLocationData = mapOf(
                        "id" to userId,
                        "name" to myName, // In a real app, fetch from auth/profile
                        "latitude" to location.latitude,
                        "longitude" to location.longitude,
                        "timestamp" to System.currentTimeMillis()
                    )
                    locationsRef.child(userId).setValue(myLocationData).await()

                    // Start listening for other users
                    listenForNearbyUsers()
                } else {
                    // Location is null, might need to request fresh location update
                    // For emulator fallback, use a dummy location if strictly needed,
                    // but we will show error for now.
                    _uiState.value = NearbyUiState.Error("Gagal mendapatkan lokasi saat ini. Pastikan GPS aktif.")
                }
            } catch (e: Exception) {
                Log.e("NearbyViewModel", "Error fetching location", e)
                _uiState.value = NearbyUiState.Error("Terjadi kesalahan: ${e.localizedMessage}")
            }
        }
    }

    private fun listenForNearbyUsers() {
        locationsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val myLoc = currentLocation ?: return
                
                val users = mutableListOf<NearbyUser>()
                for (child in snapshot.children) {
                    val id = child.child("id").getValue(String::class.java) ?: continue
                    if (id == userId) continue // Skip ourself

                    val name = child.child("name").getValue(String::class.java) ?: "Anonim"
                    val lat = child.child("latitude").getValue(Double::class.java) ?: continue
                    val lng = child.child("longitude").getValue(Double::class.java) ?: continue

                    // Calculate distance
                    val otherLoc = Location("").apply {
                        latitude = lat
                        longitude = lng
                    }
                    val distance = myLoc.distanceTo(otherLoc)
                    
                    // Tidak ada batasan jarak (menampilkan lintas negara / paling jauh sekalipun)
                    users.add(
                        NearbyUser(
                            id = id,
                            name = name,
                            latitude = lat,
                            longitude = lng,
                            distanceMeters = distance
                        )
                    )
                }
                
                // Sort by distance
                users.sortBy { it.distanceMeters }
                _uiState.value = NearbyUiState.Success(users)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("NearbyViewModel", "Database error", error.toException())
                _uiState.value = NearbyUiState.Error("Gagal memuat data teman: ${error.message}")
            }
        })
    }
}
